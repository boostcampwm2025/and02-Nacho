package com.andlife.data.util.media

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.andlife.data.util.apiCall
import com.andlife.domain.error.DataError
import com.andlife.domain.model.guestbook.MediaFile
import com.andlife.domain.model.guestbook.MediaType
import com.andlife.domain.util.MediaUploader
import com.andlife.domain.util.Result
import com.andlife.network.model.media.BatchCompleteUploadRequest
import com.andlife.network.model.media.BatchUploadMediaRequest
import com.andlife.network.model.media.ChunkUrlResponse
import com.andlife.network.model.media.CompleteFileInfoRequest
import com.andlife.network.model.media.FileUploadInfoRequest
import com.andlife.network.api.media.MediaService
import com.andlife.network.model.media.PartInfoRequest
import com.andlife.network.di.InvitationMedia
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
        @param:InvitationMedia private val okHttpClient: OkHttpClient,
    ) : MediaUploader {
        override suspend fun uploadMedias(files: List<MediaFile>): Result<List<String?>, DataError> =
            withContext(Dispatchers.IO) {
                val startRequest =
                    BatchUploadMediaRequest(
                        files =
                            files.map { mediaFile ->
                                FileUploadInfoRequest(
                                    fileName = mediaFile.fileName,
                                    fileSize = mediaFile.fileSize,
                                    mediaType = mediaFile.mediaType.name,
                                )
                            },
                    )

                val startResult = apiCall { mediaService.batchStartUpload(startRequest) }
                if (startResult is Result.Error) return@withContext startResult

                val uploadInfos = (startResult as Result.Success).data.files

                val uploadResults =
                    uploadInfos
                        .zip(files)
                        .map { (info, mediaFile) ->
                            val uri = mediaFile.uriString.toUri()
                            async {
                                try {
                                    if (info.isMultipart) {
                                        val parts =
                                            uploadMultipart(
                                                uri = uri,
                                                uploadId = info.uploadId!!,
                                                chunkUrls = info.chunkUrls!!,
                                                chunkSize = info.chunkSize!!,
                                                totalSize = mediaFile.fileSize,
                                            )

                                        if (parts == null) return@async null

                                        CompleteFileInfoRequest(
                                            mediaKey = info.mediaKey,
                                            fileName = info.fileName,
                                            uploadId = info.uploadId,
                                            parts = parts,
                                        )
                                    } else {
                                        val uploadResult =
                                            uploadSimple(
                                                uploadUrl = info.uploadUrl!!,
                                                uri = uri,
                                                mediaType = mediaFile.mediaType,
                                                fileSize = mediaFile.fileSize,
                                            )

                                        if (uploadResult is Result.Success) {
                                            CompleteFileInfoRequest(
                                                mediaKey = info.mediaKey,
                                                fileName = info.fileName,
                                            )
                                        } else {
                                            Log.e("MediaUploaderImpl", "uploadSimple 실패: $uploadResult")
                                            null
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.e("MediaUploaderImpl", "미디어 업로드 중 예외 발생", e)
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

                        val finalUrls =
                            uploadInfos.map { info ->
                                completedData
                                    .find {
                                        it.mediaKey == info.mediaKey && it.success
                                    }?.mediaUrl
                            }
                        Result.Success(finalUrls)
                    }
                }
            }

        private suspend fun uploadSimple(
            uploadUrl: String,
            uri: Uri,
            mediaType: MediaType,
            fileSize: Long,
        ): Result<Unit, DataError> =
            withContext(Dispatchers.IO) {
                try {
                    val requestBody =
                        object : RequestBody() {
                            override fun contentType() = mediaType.contentType.toMediaType()

                            override fun contentLength(): Long = fileSize

                            override fun writeTo(sink: BufferedSink) {
                                contentResolver.openInputStream(uri)?.use { inputStream ->
                                    inputStream.source().use { source ->
                                        sink.writeAll(source)
                                    }
                                } ?: throw IOException("Uri를 열 수 없음: $uri")
                            }
                        }

                    val request =
                        Request
                            .Builder()
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
            uri: Uri,
            uploadId: String,
            chunkUrls: List<ChunkUrlResponse>,
            chunkSize: Long,
            totalSize: Long,
        ): List<PartInfoRequest>? =
            withContext(Dispatchers.IO) {
                val semaphore = Semaphore(MAX_CONCURRENT_CHUNKS)

                try {
                    val parts =
                        chunkUrls
                            .map { chunkUrl ->
                                async {
                                    semaphore.withPermit {
                                        val chunkData =
                                            readChunkFromUri(
                                                uri = uri,
                                                partNumber = chunkUrl.partNumber,
                                                chunkSize = chunkSize,
                                                totalSize = totalSize,
                                            )

                                        val eTag =
                                            uploadChunkWithRetry(
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
                        val read =
                            inputStream.read(
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
                        val request =
                            Request
                                .Builder()
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
        }
    }
