package com.andlife.data.repository.fcm

import com.andlife.domain.repository.fcm.FcmTokenRepository
import javax.inject.Inject

internal class FcmTokenRepositoryImpl @Inject constructor() : FcmTokenRepository {

    override suspend fun registerTokenToServer(token: String) {
        // TODO: 서버 api 호출
    }
}
