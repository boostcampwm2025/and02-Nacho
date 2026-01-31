package com.andlife.domain.repository.user

import com.andlife.domain.error.DataError
import com.andlife.domain.error.InvitationError
import com.andlife.domain.model.auth.AuthState
import com.andlife.domain.util.Result

interface UserRepository {
    fun getUserId(): Long?
    suspend fun saveUserId(userId: Long)
    suspend fun clearUserSession()
    suspend fun login(accessToken: String): Result<Unit, DataError>
    suspend fun guestLogin(): Result<Unit, InvitationError>
    suspend fun initializeAuth(): Result<AuthState, DataError>
    suspend fun logout()
    suspend fun getUserInfo(): AuthState
}
