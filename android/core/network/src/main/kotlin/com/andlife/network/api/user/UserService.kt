package com.andlife.network.api.user

import com.andlife.network.model.BaseResponse
import com.andlife.network.model.auth.UserResponse
import retrofit2.http.GET

interface UserService {
    @GET("api/users/me")
    suspend fun getUserInfo(): BaseResponse<UserResponse>
}
