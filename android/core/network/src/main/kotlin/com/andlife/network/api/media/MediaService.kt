package com.andlife.network.api.media

import com.andlife.network.model.BaseResponse
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface MediaService {

    @POST("/api/v1/media/batch/start")
    suspend fun batchStartUpload(
        @Body request: BatchUploadMediaRequest
    ): BaseResponse<BatchUploadMediaResponse>

    @POST("/api/v1/media/batch/complete")
    suspend fun batchCompleteUpload(
        @Body request: BatchCompleteUploadRequest
    ): BaseResponse<BatchCompleteUploadResponse>

    @DELETE("/api/v1/media/{mediaKey}")
    suspend fun deleteMedia(
        @Path("mediaKey") mediaKey: String
    ): BaseResponse<DeleteMediaResponse>
}
