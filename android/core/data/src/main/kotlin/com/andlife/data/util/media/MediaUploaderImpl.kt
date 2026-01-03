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

    override suspend fun uploadMedia(
        file: File,
        mediaType: MediaType
    ): Result<String, DataError> = withContext(Dispatchers.IO) {
        val uploadUrlResult = apiCall {
            mediaService.uploadMedia(
                UploadMediaRequest(mediaType = mediaType.name, fileName = file.name)
            )
        }

        if (uploadUrlResult is Result.Error) return@withContext uploadUrlResult
        val uploadData = (uploadUrlResult as Result.Success).data

        val uploadResult = uploadToR2(uploadData.uploadUrl, file, mediaType)
        if (uploadResult is Result.Error) return@withContext uploadResult

        val completeResult = apiCall {
            mediaService.completeUpload(
                CompleteUploadRequest(mediaKey = uploadData.mediaKey, mediaType = mediaType.name)
            )
        }

        return@withContext when (completeResult) {
            is Result.Error -> completeResult
            is Result.Success -> {
                val mediaUrl = completeResult.data.mediaUrl
                if (mediaUrl != null) Result.Success(mediaUrl)
                else Result.Error(DataError.Network.UNKNOWN, "Media URL is null")
            }
        }
    }

    override suspend fun uploadMediaBatch(
        files: List<Pair<File, MediaType>>
    ): Result<List<String?>, DataError> = withContext(Dispatchers.IO) {
        val batchRequest = BatchUploadMediaRequest(
            files = files.map { (file, type) ->
                FileInfo(mediaType = type.name, fileName = file.name)
            }
        )

        // 서버로부터 업로드 URL들 받아오기
        val uploadUrlsResult = apiCall { mediaService.batchUploadMedia(batchRequest) }
        if (uploadUrlsResult is Result.Error) return@withContext uploadUrlsResult

        val uploadInfos = (uploadUrlsResult as Result.Success).data.files

        // R2 병렬 업로드 (실패 시 null 반환)
        val uploadResults = uploadInfos.zip(files).map { (info, filePair) ->
            async {
                val result = uploadToR2(info.uploadUrl, filePair.first, filePair.second)
                if (result is Result.Success) info else null
            }
        }.awaitAll()

        // 업로드에 성공한 항목들만 모아서 완료 요청 (성공한 게 하나도 없더라도 API 구조에 따라 호출 가능)
        val successfulInfos = uploadResults.filterNotNull()

        // 만약 성공한 게 하나도 없다면 바로 빈 리스트 반환할 수도 있음
        if (successfulInfos.isEmpty()) {
            return@withContext Result.Success(List(files.size) { null })
        }

        val completeRequest = BatchCompleteUploadRequest(
            files = successfulInfos.map { info ->
                FileKeyInfo(mediaKey = info.mediaKey, fileName = info.fileName)
            }
        )

        val completeResult = apiCall { mediaService.batchCompleteUpload(completeRequest) }

        return@withContext when (completeResult) {
            is Result.Error -> completeResult
            is Result.Success -> {
                val completedData = completeResult.data.files

                val finalUrls = uploadInfos.map { info ->
                    completedData.find { it.mediaKey == info.mediaKey }?.mediaUrl
                }
                Result.Success(finalUrls)
            }
        }
    }

    private suspend fun uploadToR2(
        uploadUrl: String,
        file: File,
        mediaType: MediaType
    ): Result<Unit, DataError> = withContext(Dispatchers.IO) {
        try {
            val requestBody = file.asRequestBody(mediaType.contentType.toMediaType())
            val request = Request.Builder().url(uploadUrl).put(requestBody).build()

            okHttpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    Result.Success(Unit)
                } else {
                    Result.Error(DataError.Network.UNKNOWN, "R2 upload failed")
                }
            }
        } catch (e: Exception) {
            Result.Error(DataError.Network.UNKNOWN, e.message ?: "Unknown error")
        }
    }
}
