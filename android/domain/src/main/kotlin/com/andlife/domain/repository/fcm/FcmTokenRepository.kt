package com.andlife.domain.repository.fcm

interface FcmTokenRepository {
    suspend fun registerTokenToServer(token: String)
}
