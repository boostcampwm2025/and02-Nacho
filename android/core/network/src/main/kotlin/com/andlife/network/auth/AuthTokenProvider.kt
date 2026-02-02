package com.andlife.network.auth

interface AuthTokenProvider {
    fun getInvitationIds(): List<Long>
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun saveTokens(accessToken: String, refreshToken: String)
    suspend fun clearTokens()
}
