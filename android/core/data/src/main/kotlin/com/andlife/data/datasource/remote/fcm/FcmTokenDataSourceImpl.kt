package com.andlife.data.datasource.remote.fcm

import com.andlife.data.util.apiCall
import com.andlife.domain.error.DataError
import com.andlife.domain.util.Result
import com.andlife.network.api.user.UserService
import com.andlife.network.model.user.RegisterFcmTokenRequest
import com.andlife.network.model.user.RegisterFcmTokenResponse
import javax.inject.Inject

internal class FcmTokenDataSourceImpl @Inject constructor(
    private val userService: UserService
) : FcmTokenDataSource {

    override suspend fun registerTokenToServer(token: String): Result<RegisterFcmTokenResponse, DataError> {
        val request = RegisterFcmTokenRequest(fcmToken = token)
        return apiCall { userService.registerFcmToken(request) }
    }
}