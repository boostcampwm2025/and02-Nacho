package com.andlife.domain.repository.user

interface UserRepository {
    fun getUserId(): Long?
    suspend fun saveUserId(userId: Long)
    suspend fun clearUserSession()
}
