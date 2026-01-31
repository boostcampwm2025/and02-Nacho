package com.andlife.domain.repository.auth

import com.andlife.domain.model.auth.AuthEvent
import com.andlife.domain.model.auth.AuthState
import com.andlife.domain.model.auth.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AuthStateManager {
    val authState: StateFlow<AuthState>

    val authEvent: Flow<AuthEvent>

    suspend fun setAuthenticated(user: User)
    suspend fun setGuest()
    suspend fun setLoading()

    suspend fun navigateToLogin()
    suspend fun navigateToHome()
}
