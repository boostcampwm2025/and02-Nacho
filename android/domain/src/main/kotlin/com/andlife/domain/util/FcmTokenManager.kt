package com.andlife.domain.util

interface FcmTokenManager {
    fun syncToken(token: String)
    suspend fun getToken(): String?
}
