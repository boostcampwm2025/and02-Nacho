package com.andlife.data.util.media

import com.andlife.data.util.apiCall
import com.andlife.domain.error.DataError
import com.andlife.domain.repository.MediaType
import com.andlife.domain.util.Result
import com.andlife.network.api.media.BatchCompleteUploadRequest
import com.andlife.network.api.media.BatchUploadMediaRequest
import com.andlife.network.api.media.CompleteUploadRequest
import com.andlife.network.api.media.FileInfo
import com.andlife.network.api.media.FileKeyInfo
import com.andlife.network.api.media.MediaService
import com.andlife.network.api.media.UploadMediaRequest
import com.andlife.network.di.MediaOkHttp
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class MediaUploaderImpl @Inject constructor(
    private val mediaService: MediaService,
    @param:MediaOkHttp private val okHttpClient: OkHttpClient
) : MediaUploader {

    companion object {
        private const val TAG = "MediaUploader"
    }

    override suspend fun uploadMedia(
        file: File,
        mediaType: MediaType
    ): Result<String, DataError> {
        val uploadUrlResult = apiCall {
            mediaService.uploadMedia(
                UploadMediaRequest(
                    mediaType = mediaType.name,
                    fileName = file.name
                )
            )
        }

        when (uploadUrlResult) {
            is Result.Error -> {
                return uploadUrlResult
            }
            is Result.Success -> {
                val uploadData = uploadUrlResult.data
                val uploadResult = uploadToR2(
                    uploadData.uploadUrl,
                    file,
                    mediaType
                )

                if (uploadResult is Result.Error) {
                    return uploadResult
                }
                val completeResult = apiCall {
                    mediaService.completeUpload(
                        CompleteUploadRequest(
                            mediaKey = uploadData.mediaKey,
                            mediaType = mediaType.name
                        )
                    )
                }

                return when (completeResult) {
                    is Result.Error -> {
                        completeResult
                    }
                    is Result.Success -> {
                        val mediaUrl = completeResult.data.mediaUrl
                        if (mediaUrl != null) {
                            Result.Success(mediaUrl)
                        } else {
                            Result.Error(
                                DataError.Network.UNKNOWN,
                                "Media URL is null"
                            )
                        }
                    }
                }
            }
        }
    }

    override suspend fun uploadMediaBatch(
        files: List<Pair<File, MediaType>>
    ): Result<List<String>, DataError> {
        return withContext(Dispatchers.IO) {
            val batchRequest = BatchUploadMediaRequest(
                files = files.map { (file, type) ->
                    FileInfo(
                        mediaType = type.name,
                        fileName = file.name
                    )
                }
            )

            val uploadUrlsResult = apiCall {
                mediaService.batchUploadMedia(batchRequest)
            }

            when (uploadUrlsResult) {
                is Result.Error -> {
                    return@withContext uploadUrlsResult
                }
                is Result.Success -> {
                    val uploadInfos = uploadUrlsResult.data.files
                    val uploadResults = uploadInfos.zip(files).mapIndexed { index, (info, filePair) ->
                        async {
                            uploadToR2(info.uploadUrl, filePair.first, filePair.second)
                        }
                    }.awaitAll()

                    // 업로드 실패한 항목 체크
                    val firstError = uploadResults.firstOrNull { it is Result.Error }
                    if (firstError is Result.Error) {
                        return@withContext firstError
                    }
                    val completeRequest = BatchCompleteUploadRequest(
                        files = uploadInfos.map { info ->
                            FileKeyInfo(
                                mediaKey = info.mediaKey,
                                fileName = info.fileName
                            )
                        }
                    )

                    val completeResult = apiCall {
                        mediaService.batchCompleteUpload(completeRequest)
                    }

                    when (completeResult) {
                        is Result.Error -> {
                            completeResult
                        }
                        is Result.Success -> {
                            val mediaUrls = completeResult.data.files
                                .sortedBy { uploadInfos.indexOfFirst { info -> info.mediaKey == it.mediaKey } }
                                .mapNotNull { it.mediaUrl }

                            if (mediaUrls.size == files.size) {
                                Result.Success(mediaUrls)
                            } else {
                                Result.Error(
                                    DataError.Network.UNKNOWN,
                                    "Some media URLs are missing"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun uploadToR2(
        uploadUrl: String,
        file: File,
        mediaType: MediaType
    ): Result<Unit, DataError> = withContext(Dispatchers.IO) {
        try {
            val contentType = when (mediaType) {
                MediaType.IMAGE -> "image/jpeg"
                MediaType.VIDEO -> "video/mp4"
                MediaType.AUDIO -> "audio/mpeg"
            }

            val requestBody = file.asRequestBody(contentType.toMediaType())
            val request = Request.Builder()
                .url(uploadUrl)
                .put(requestBody)
                .build()
            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                val errorBody = response.body?.string()
                Result.Error(
                    DataError.Network.UNKNOWN,
                    "R2 upload failed: ${response.code} - $errorBody"
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(
                DataError.Network.UNKNOWN,
                "R2 upload exception: ${e.message}"
            )
        }
    }
}
