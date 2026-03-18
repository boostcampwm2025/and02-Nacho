package com.andlife.data.datasource.remote.fcm

import com.andlife.data.util.apiCall
import com.andlife.domain.error.DataError
import com.andlife.domain.util.Result
import com.andlife.network.api.user.UserService
import com.andlife.network.model.user.FcmTokenRequest
import com.andlife.network.model.user.FcmTokenResponse
import javax.inject.Inject

internal class FcmTokenDataSourceImpl @Inject constructor(
    private val userService: UserService
) : FcmTokenDataSource {

    override suspend fun putTokenToServer(token: String): Result<FcmTokenResponse, DataError> {
        val request = FcmTokenRequest(fcmToken = token)
        return apiCall { userService.putFcmToken(request) }
    }
}
