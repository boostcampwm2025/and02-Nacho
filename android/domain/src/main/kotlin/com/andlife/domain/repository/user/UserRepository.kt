package com.andlife.domain.repository.user

import com.andlife.domain.error.DataError
import com.andlife.domain.error.InvitationError
import com.andlife.domain.model.auth.AuthState
import com.andlife.domain.util.Result

interface UserRepository {
    suspend fun login(accessToken: String): Result<Unit, DataError>
    suspend fun loginWithTestUser(): Result<Unit, DataError>
    suspend fun guestLogin(): Result<Unit, InvitationError>
    suspend fun initializeAuth(): Result<AuthState, DataError>
    suspend fun logout()
    suspend fun signOut(): Result<Unit, DataError>
    suspend fun getUserInfo(): AuthState
    suspend fun isWifiDialogDismissed(): Boolean
    suspend fun setWifiDialogDismissed()
    suspend fun isFirstDownloadDone(): Boolean
    suspend fun setFirstDownloadDone()
}
