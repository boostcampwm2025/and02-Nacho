package com.andlife.network.api.auth

import com.andlife.network.model.BaseResponse
import com.andlife.network.model.auth.AuthRequest
import com.andlife.network.model.auth.AuthResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("api/auth/login")
    suspend fun login(
        @Body request: AuthRequest
    ): BaseResponse<AuthResponse>
}
