package com.andlife.data.repository.fcm

import com.andlife.data.datasource.remote.fcm.FcmTokenDataSource
import com.andlife.domain.repository.fcm.FcmTokenRepository
import javax.inject.Inject

internal class FcmTokenRepositoryImpl @Inject constructor(
    private val fcmTokenDataSource: FcmTokenDataSource
) : FcmTokenRepository {

    override suspend fun putTokenToServer(token: String) {
        fcmTokenDataSource.putTokenToServer(token)
    }
}
