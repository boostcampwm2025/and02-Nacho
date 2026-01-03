package com.andlife.network.api.media

import com.andlife.network.model.BaseResponse
import retrofit2.http.POST

interface MediaService {
    @POST("/api/v1/media/start")
    suspend fun uploadMedia(): BaseResponse<String>

    @POST("/api/v1/media/complete")
    suspend fun completeUploadMedia(): BaseResponse<Unit>
}
