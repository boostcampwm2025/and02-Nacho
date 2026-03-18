package com.andlife.domain.repository.fcm

interface FcmTokenRepository {
    suspend fun putTokenToServer(token: String)
}
