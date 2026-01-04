package com.andlife.data.util.media

import com.andlife.data.util.apiCall
import com.andlife.domain.error.DataError
import com.andlife.domain.model.MediaType
import com.andlife.domain.util.Result
import com.andlife.network.api.media.BatchCompleteUploadRequest
import com.andlife.network.api.media.BatchUploadMediaRequest
import com.andlife.network.api.media.ChunkUrl
import com.andlife.network.api.media.CompleteFileInfo
import com.andlife.network.api.media.FileUploadInfo
import com.andlife.network.api.media.MediaService
import com.andlife.network.api.media.PartInfo
import com.andlife.network.di.MediaOkHttp
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException

class MediaUploaderImpl @Inject constructor(
    private val mediaService: MediaService,
    @param:MediaOkHttp private val okHttpClient: OkHttpClient
) : MediaUploader {

    override suspend fun uploadMedias(
        files: List<MediaFile>
    ): Result<List<String?>, DataError> = withContext(Dispatchers.IO) {

        val startRequest = BatchUploadMediaRequest(
            files = files.map { mediaFile ->
                FileUploadInfo(
                    fileName = mediaFile.file.name,
                    fileSize = mediaFile.file.length(),
                    mediaType = mediaFile.mediaType.name
                )
            }
        )

        val startResult = apiCall { mediaService.batchStartUpload(startRequest) }
        if (startResult is Result.Error) return@withContext startResult

        val uploadInfos = (startResult as Result.Success).data.files

        val uploadResults = uploadInfos.zip(files).map { (info, mediaFile) ->
            async {
                try {
                    if (info.isMultipart) {
                        val parts = uploadMultipart(
                            file = mediaFile.file,
                            uploadId = info.uploadId!!,
                            chunkUrls = info.chunkUrls!!,
                            chunkSize = info.chunkSize!!
                        )

                        CompleteFileInfo(
                            mediaKey = info.mediaKey,
                            fileName = info.fileName,
                            uploadId = info.uploadId,
                            parts = parts
                        )
                    } else {
                        val uploadResult = uploadSimple(
                            uploadUrl = info.uploadUrl!!,
                            file = mediaFile.file,
                            mediaType = mediaFile.mediaType,
                        )

                        if (uploadResult is Result.Success) {
                            CompleteFileInfo(
                                mediaKey = info.mediaKey,
                                fileName = info.fileName,
                            )
                        } else {
                            null
                        }
                    }
                } catch (e: Exception) {
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

                // 원래 순서대로 URL 매핑 (실패한 건 null)
                val finalUrls = uploadInfos.map { info ->
                    completedData.find {
                        it.mediaKey == info.mediaKey && it.success
                    }?.mediaUrl
                }
                Result.Success(finalUrls)
            }
        }
    }

    private suspend fun uploadSimple(
        uploadUrl: String,
        file: File,
        mediaType: MediaType
    ): Result<Unit, DataError> = withContext(Dispatchers.IO) {
        try {
            val requestBody = file.asRequestBody(mediaType.contentType.toMediaType())
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
                        "Simple upload failed: ${response.code}"
                    )
                }
            }
        } catch (e: Exception) {
            Result.Error(DataError.Network.UNKNOWN, e.message ?: "Unknown error")
        }
    }

    private suspend fun uploadMultipart(
        file: File,
        uploadId: String,
        chunkUrls: List<ChunkUrl>,
        chunkSize: Long
    ): List<PartInfo> = withContext(Dispatchers.IO) {
        val parts = mutableListOf<PartInfo>()

        file.inputStream().use { inputStream ->
            for (chunkUrl in chunkUrls) {
                val buffer = ByteArray(chunkSize.toInt())
                val bytesRead = inputStream.read(buffer)
                val chunkData = if (bytesRead < chunkSize) {
                    buffer.copyOf(bytesRead)
                } else {
                    buffer
                }

                val eTag = uploadChunkWithRetry(
                    url = chunkUrl.uploadUrl,
                    data = chunkData,
                    partNumber = chunkUrl.partNumber
                )

                parts.add(PartInfo(
                    partNumber = chunkUrl.partNumber,
                    eTag = eTag
                ))
            }
        }

        parts
    }

    private suspend fun uploadChunkWithRetry(
        url: String,
        data: ByteArray,
        partNumber: Int,
        maxRetries: Int = 3
    ): String = withContext(Dispatchers.IO) {
        var lastException: Exception? = null

        repeat(maxRetries) { attempt ->
            try {
                val request = Request.Builder()
                    .url(url)
                    .put(data.toRequestBody("application/octet-stream".toMediaType()))
                    .build()

                val response = okHttpClient.newCall(request).execute()

                if (response.isSuccessful) {
                    val eTag = response.header("ETag")?.trim('"')
                        ?: throw IllegalStateException("No ETag in response")
                    return@withContext eTag
                } else {
                    throw IOException("Part $partNumber upload failed: ${response.code}")
                }

            } catch (e: Exception) {
                lastException = e
                if (attempt < maxRetries - 1) {
                    delay(1000L * (attempt + 1))
                }
            }
        }

        throw lastException ?: IOException("Upload failed for part $partNumber")
    }
}
