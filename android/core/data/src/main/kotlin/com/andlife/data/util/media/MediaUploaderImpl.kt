package com.andlife.data.util.media

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.andlife.data.util.apiCall
import com.andlife.domain.error.DataError
import com.andlife.domain.model.guestbook.MediaFile
import com.andlife.domain.model.guestbook.MediaType
import com.andlife.domain.model.guestbook.UploadGuestBookState
import com.andlife.domain.util.MediaUploader
import com.andlife.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.andlife.network.model.media.BatchCompleteUploadRequest
import com.andlife.network.model.media.BatchUploadMediaRequest
import com.andlife.network.model.media.ChunkUrlResponse
import com.andlife.network.model.media.CompleteFileInfoRequest
import com.andlife.network.model.media.FileUploadInfoRequest
import com.andlife.network.api.media.MediaService
import com.andlife.network.model.media.PartInfoRequest
import com.andlife.network.di.NachoMedia
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.BufferedSink
import okio.source
import java.io.FileInputStream
import java.io.IOException

class MediaUploaderImpl
@Inject
constructor(
    private val contentResolver: ContentResolver,
    private val mediaService: MediaService,
    private val imageCompressor: ImageCompressor,
    @param:NachoMedia private val okHttpClient: OkHttpClient,
) : MediaUploader {
    override suspend fun uploadMedias(files: List<MediaFile>): Result<List<String?>, DataError> =
        withContext(Dispatchers.IO) {
            // 파일 크기 체크
            var totalFileSize = 0L
            files.forEach { file ->
                totalFileSize += file.fileSize
                if (totalFileSize > MAX_MEDIA_SIZE_BYTES) {
                    Log.w("MediaUploaderImpl", "파일 크기 초과로 업로드 거부됨")
                    return@withContext Result.Error(
                        DataError.Validation.FILE_TOO_LARGE,
                        "파일 크기가 ${MAX_MEDIA_SIZE_BYTES / (1024 * 1024)}MB를 초과해 업로드에 실패했습니다."
                    )
                }
            }
            val compressedDataList = files.map { file ->
                if (file.mediaType == MediaType.IMAGE) {
                    imageCompressor.compressImage(uri = file.uriString.toUri())
                } else {
                    null
                }
            }

            val startRequest = BatchUploadMediaRequest(
                files = files.mapIndexed { index, mediaFile ->
                    val compressed = compressedDataList[index]
                    FileUploadInfoRequest(
                        fileName = if (compressed != null) {
                            mediaFile.fileName.replaceAfterLast('.', "webp")
                        } else {
                            mediaFile.fileName
                        },
                        fileSize = compressed?.size?.toLong() ?: mediaFile.fileSize,
                        mediaType = mediaFile.mediaType.name,
                    )
                },
            )

            val startResult = apiCall { mediaService.batchStartUpload(startRequest) }
            if (startResult is Result.Error) return@withContext startResult

            val uploadInfos = (startResult as Result.Success).data.files

            val uploadResults = uploadInfos.mapIndexed { index, response ->
                val file = files[index]
                val data = compressedDataList[index]

                async {
                    try {
                        val currentSize = data?.size?.toLong() ?: file.fileSize
                        val uri = if (data == null) file.uriString.toUri() else null

                        if (response.isMultipart) {
                            val parts = uploadMultipart(
                                data = data,
                                uri = uri,
                                uploadId = response.uploadId!!,
                                chunkUrls = response.chunkUrls!!,
                                chunkSize = response.chunkSize!!,
                                totalSize = currentSize,
                            )

                            if (parts == null) return@async null

                            CompleteFileInfoRequest(
                                mediaKey = response.mediaKey,
                                fileName = response.fileName,
                                uploadId = response.uploadId,
                                parts = parts,
                            )
                        } else {
                            val uploadResult = uploadSimple(
                                uploadUrl = response.uploadUrl!!,
                                data = data,
                                uri = uri,
                                mediaType = file.mediaType,
                                fileSize = currentSize,
                            )

                            if (uploadResult is Result.Success) {
                                CompleteFileInfoRequest(
                                    mediaKey = response.mediaKey,
                                    fileName = response.fileName,
                                )
                            } else {
                                Log.e("MediaUploaderImpl", "uploadSimple 실패: $uploadResult")
                                null
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("MediaUploaderImpl", "업로드 중 오류: ${file.fileName}", e)
                        null
                    }
                }
            }.awaitAll()

            val successfulFiles = uploadResults.filterNotNull()

            if (successfulFiles.isEmpty()) {
                return@withContext Result.Success(List(files.size) { null })
            }

            val completeRequest = BatchCompleteUploadRequest(files = successfulFiles)
            val completeResult = apiCall { mediaService.batchCompleteUpload(completeRequest) }

            return@withContext when (completeResult) {
                is Result.Error -> completeResult
                is Result.Success -> {
                    val completedData = completeResult.data.files

                    // 업로드 요청 순서대로 결과 매핑 (실패한 경우 null)
                    val finalUrls = uploadInfos.map { info ->
                        completedData
                            .find {
                                it.mediaKey == info.mediaKey && it.success
                            }?.mediaUrl
                    }
                    Result.Success(finalUrls)
                }
            }
        }

    override fun uploadMediasWithProgress(files: List<MediaFile>): Flow<UploadGuestBookState> = flow {
        try {
            emit(
                UploadGuestBookState.Progress(
                    percent = 0,
                    currentFileName = "파일 크기 체크 중...",
                )
            )

            // 파일 크기 체크
            var totalFileSize = 0L
            files.forEach { file ->
                totalFileSize += file.fileSize
                if (totalFileSize > MAX_MEDIA_SIZE_BYTES) {
                    emit(UploadGuestBookState.Failure("파일 크기가 ${MAX_MEDIA_SIZE_BYTES / (1024 * 1024)}MB를 초과해 업로드에 실패했습니다."))
                    return@flow
                }
            }

            emit(
                UploadGuestBookState.Progress(
                    percent = 10,
                    currentFileName = "이미지 압축 중...",
                )
            )

            // 압축 처리
            val compressedDataList = files.map { file ->
                if (file.mediaType == MediaType.IMAGE) {
                    imageCompressor.compressImage(uri = file.uriString.toUri())
                } else {
                    null
                }
            }

            emit(
                UploadGuestBookState.Progress(
                    percent = 20,
                    currentFileName = "업로드 중...",
                )
            )

            // 업로드 시작
            val startRequest = BatchUploadMediaRequest(
                files = files.mapIndexed { index, mediaFile ->
                    val compressed = compressedDataList[index]
                    FileUploadInfoRequest(
                        fileName = if (compressed != null) {
                            mediaFile.fileName.replaceAfterLast('.', "webp")
                        } else {
                            mediaFile.fileName
                        },
                        fileSize = compressed?.size?.toLong() ?: mediaFile.fileSize,
                        mediaType = mediaFile.mediaType.name,
                    )
                },
            )

            val startResult = apiCall { mediaService.batchStartUpload(startRequest) }
            if (startResult is Result.Error) {
                emit(UploadGuestBookState.Failure(startResult.message ?: "업로드 시작 실패"))
                return@flow
            }

            val uploadInfos = (startResult as Result.Success).data.files
            emit(
                UploadGuestBookState.Progress(
                    percent = 30,
                    currentFileName = "업로드 정보 수신 완료. 파일 개수: ${uploadInfos.size}"
                )
            )

            // 순차적으로 업로드하면서 진행률 업데이트
            val uploadResults = mutableListOf<CompleteFileInfoRequest?>()
            uploadInfos.forEachIndexed { index, response ->
                val file = files[index]
                val data = compressedDataList[index]

                try {
                    val baseProgress = 30 + (index / files.size) * 60
                    emit(
                        UploadGuestBookState.Progress(
                            percent = baseProgress,
                            currentFileName = file.fileName,
                            currentOrder = index + 1,
                            totalCount = files.size,
                        )
                    )

                    val currentSize = data?.size?.toLong() ?: file.fileSize
                    val uri = if (data == null) file.uriString.toUri() else null

                    val result = if (response.isMultipart) {
                        val parts = uploadMultipart(
                            data = data,
                            uri = uri,
                            uploadId = response.uploadId!!,
                            chunkUrls = response.chunkUrls!!,
                            chunkSize = response.chunkSize!!,
                            totalSize = currentSize,
                        )

                        if (parts == null) {
                            null
                        } else {
                            CompleteFileInfoRequest(
                                mediaKey = response.mediaKey,
                                fileName = response.fileName,
                                uploadId = response.uploadId,
                                parts = parts,
                            )
                        }
                    } else {
                        val uploadResult = uploadSimple(
                            uploadUrl = response.uploadUrl!!,
                            data = data,
                            uri = uri,
                            mediaType = file.mediaType,
                            fileSize = currentSize,
                        )

                        if (uploadResult is Result.Success) {
                            CompleteFileInfoRequest(
                                mediaKey = response.mediaKey,
                                fileName = response.fileName,
                            )
                        } else {
                            null
                        }
                    }

                    uploadResults.add(result)

                } catch (e: Exception) {
                    Log.e("MediaUploaderImpl", "업로드 중 오류: ${file.fileName}", e)
                    uploadResults.add(null)
                }
            }

            emit(
                UploadGuestBookState.Progress(
                    percent = 90,
                    currentFileName = "업로드 완료 요청 중..."
                )
            )

            val successfulFiles = uploadResults.filterNotNull()
            if (successfulFiles.isEmpty()) {
                emit(UploadGuestBookState.Failure("모든 파일 업로드가 실패했습니다"))
                return@flow
            }

            // 완료 요청
            val completeRequest = BatchCompleteUploadRequest(files = successfulFiles)
            val completeResult = apiCall { mediaService.batchCompleteUpload(completeRequest) }

            when (completeResult) {
                is Result.Error -> {
                    emit(UploadGuestBookState.Failure(completeResult.message ?: "업로드 완료 실패"))
                }

                is Result.Success -> {
                    val completedData = completeResult.data.files
                    val finalUrls = uploadInfos.map { info ->
                        completedData
                            .find { it.mediaKey == info.mediaKey && it.success }
                            ?.mediaUrl
                    }
                    emit(UploadGuestBookState.Success(finalUrls))
                }
            }
        } catch (e: Exception) {
            emit(UploadGuestBookState.Failure(e.message ?: "알 수 없는 오류"))
        }
    }

    private suspend fun uploadSimple(
        uploadUrl: String,
        data: ByteArray? = null,
        uri: Uri? = null,
        mediaType: MediaType,
        fileSize: Long,
    ): Result<Unit, DataError> =
        withContext(Dispatchers.IO) {
            try {
                val requestBody = object : RequestBody() {
                    override fun contentType() = mediaType.contentType.toMediaType()

                    override fun contentLength(): Long = fileSize

                    override fun writeTo(sink: BufferedSink) {
                        if (data != null) {
                            sink.write(data)
                        } else {
                            contentResolver.openInputStream(uri!!)?.use { inputStream ->
                                inputStream.source().use { source ->
                                    sink.writeAll(source)
                                }
                            } ?: throw IOException("Uri를 열 수 없음: $uri")
                        }
                    }
                }

                val request = Request.Builder()
                    .url(uploadUrl)
                    .put(requestBody)
                    .build()

                okHttpClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        Result.Success(Unit)
                    } else {
                        Result.Error(
                            DataError.Network.UNKNOWN,
                            "uploadSimple 실패: ${response.code}",
                        )
                    }
                }
            } catch (e: Exception) {
                Result.Error(DataError.Network.UNKNOWN, e.message ?: "알 수 없는 에러")
            }
        }

    private suspend fun uploadMultipart(
        data: ByteArray? = null,
        uri: Uri? = null,
        uploadId: String,
        chunkUrls: List<ChunkUrlResponse>,
        chunkSize: Long,
        totalSize: Long,
    ): List<PartInfoRequest>? =
        withContext(Dispatchers.IO) {
            val semaphore = Semaphore(MAX_CONCURRENT_CHUNKS)

            try {
                val parts = chunkUrls
                    .map { chunkUrl ->
                        async {
                            semaphore.withPermit {
                                val chunkData = if (data != null) {
                                    readChunkFromByteArray(
                                        data = data,
                                        partNumber = chunkUrl.partNumber,
                                        chunkSize = chunkSize,
                                        totalSize = totalSize,
                                    )
                                } else {
                                    readChunkFromUri(
                                        uri = uri!!,
                                        partNumber = chunkUrl.partNumber,
                                        chunkSize = chunkSize,
                                        totalSize = totalSize,
                                    )
                                }

                                val eTag = uploadChunkWithRetry(
                                    url = chunkUrl.uploadUrl,
                                    data = chunkData,
                                    partNumber = chunkUrl.partNumber,
                                )

                                PartInfoRequest(partNumber = chunkUrl.partNumber, eTag = eTag)
                            }
                        }
                    }.awaitAll()
                parts
            } catch (e: Exception) {
                Log.e("MediaUploaderImpl", "멀티파트 업로드 실패", e)
                null
            }
        }

    private fun readChunkFromByteArray(
        data: ByteArray,
        partNumber: Int,
        chunkSize: Long,
        totalSize: Long,
    ): ByteArray {
        val offset = ((partNumber - 1) * chunkSize).toInt()
        val remainingBytes = totalSize - offset
        val bytesToRead = minOf(chunkSize, remainingBytes).toInt()

        return data.copyOfRange(offset, offset + bytesToRead)
    }

    private fun readChunkFromUri(
        uri: Uri,
        partNumber: Int,
        chunkSize: Long,
        totalSize: Long,
    ): ByteArray =
        contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
            FileInputStream(pfd.fileDescriptor).use { inputStream ->
                val offset = (partNumber - 1) * chunkSize
                val remainingBytes = totalSize - offset
                val bytesToRead = minOf(chunkSize, remainingBytes).toInt()

                // FileChannel을 사용한 random access
                inputStream.channel.position(offset)

                val buffer = ByteArray(bytesToRead)
                var totalRead = 0

                while (totalRead < bytesToRead) {
                    val read = inputStream.read(
                        buffer,
                        totalRead,
                        bytesToRead - totalRead,
                    )
                    if (read == -1) break
                    totalRead += read
                }

                buffer
            }
        } ?: throw IOException("FileDescriptor로 열 수 없음: $uri")

    private suspend fun uploadChunkWithRetry(
        url: String,
        data: ByteArray,
        partNumber: Int,
        maxRetries: Int = 3,
    ): String =
        withContext(Dispatchers.IO) {
            var lastException: Exception? = null

            repeat(maxRetries) { attempt ->
                try {
                    val request = Request.Builder()
                        .url(url)
                        .put(data.toRequestBody("application/octet-stream".toMediaType()))
                        .build()

                    okHttpClient.newCall(request).execute().use { response ->
                        if (response.isSuccessful) {
                            return@withContext response.header("ETag")?.trim('"')
                                ?: throw IllegalStateException("ETag 헤더 누락")
                        } else {
                            throw IOException("Part $partNumber 업로드 실패: ${response.code}")
                        }
                    }
                } catch (e: Exception) {
                    lastException = e
                    if (attempt < maxRetries - 1) {
                        delay(1000L * (attempt + 1))
                    }
                }
            }

            throw lastException ?: IOException("Part $partNumber 업로드 실패...")
        }

    companion object {
        private const val MAX_CONCURRENT_CHUNKS = 3
        private const val MAX_MEDIA_SIZE_BYTES = 500 * 1024 * 1024L // 200MB
    }
}
