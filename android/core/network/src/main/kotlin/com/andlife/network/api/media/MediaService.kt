package com.andlife.network.api.media

import com.andlife.network.model.BaseResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface MediaService {

    @POST("/api/v1/media/start")
    suspend fun uploadMedia(
        @Body request: UploadMediaRequest
    ): BaseResponse<UploadMediaResponse>

    @POST("/api/v1/media/complete")
    suspend fun completeUpload(
        @Body request: CompleteUploadRequest
    ): BaseResponse<CompleteUploadResponse>

    @DELETE("/api/v1/media/{mediaKey}")
    suspend fun deleteMedia(
        @Path("mediaKey") mediaKey: String
    ): BaseResponse<CompleteUploadResponse>
}
