package com.andlife.data.datasource.remote.fcm

import com.andlife.domain.error.DataError
import com.andlife.domain.util.Result
import com.andlife.network.model.user.RegisterFcmTokenResponse

internal interface FcmTokenDataSource {
    suspend fun registerTokenToServer(token: String): Result<RegisterFcmTokenResponse, DataError>
}
