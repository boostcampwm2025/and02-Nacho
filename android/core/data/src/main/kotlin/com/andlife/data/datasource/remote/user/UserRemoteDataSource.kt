package com.andlife.data.datasource.remote.user

import com.andlife.domain.error.DataError
import com.andlife.domain.util.Result
import com.andlife.network.model.auth.AuthRequest
import com.andlife.network.model.auth.AuthResponse
import com.andlife.network.model.auth.UserResponse

interface UserRemoteDataSource {
    suspend fun login(request: AuthRequest): Result<AuthResponse, DataError>
    suspend fun loginWithTestUser(): Result<AuthResponse, DataError>
    suspend fun getUserInfo(): Result<UserResponse, DataError>
    suspend fun reissue(refreshToken: String): Result<AuthResponse, DataError>
    suspend fun syncInvitations(invitationIds: List<Long>): Result<Unit, DataError>
}
