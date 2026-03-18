package com.andlife.network.api.user

import com.andlife.network.model.BaseResponse
import com.andlife.network.model.auth.UserResponse
import com.andlife.network.model.user.RegisterFcmTokenRequest
import com.andlife.network.model.user.RegisterFcmTokenResponse
import com.andlife.network.model.user.UpdateProfileRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface UserService {
    @GET("api/users/me")
    suspend fun getUserInfo(): BaseResponse<UserResponse>

    @DELETE("api/users")
    suspend fun signOut(): BaseResponse<UserResponse>

    @PATCH("api/users/me")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest
    ): BaseResponse<UserResponse>

    @POST("api/users/fcm-token")
    suspend fun putFcmToken(
        @Body request: RegisterFcmTokenRequest
    ): BaseResponse<RegisterFcmTokenResponse>
}
