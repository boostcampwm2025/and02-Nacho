package com.andlife.data.datasource.remote.user

import com.andlife.data.util.apiCall
import com.andlife.domain.error.DataError
import com.andlife.domain.util.Result
import com.andlife.domain.util.map
import com.andlife.network.api.auth.AuthService
import com.andlife.network.api.invitation.InvitationService
import com.andlife.network.api.user.UserService
import com.andlife.network.model.auth.AuthRequest
import com.andlife.network.model.auth.AuthResponse
import com.andlife.network.model.auth.RefreshTokenRequest
import com.andlife.network.model.auth.UserResponse
import javax.inject.Inject

class UserRemoteDataSourceImpl @Inject constructor(
    private val authService: AuthService,
    private val userService: UserService,
    private val invitationService: InvitationService
) : UserRemoteDataSource {
    override suspend fun login(request: AuthRequest): Result<AuthResponse, DataError> =
        apiCall { authService.login(request) }

    override suspend fun loginWithTestUser(): Result<AuthResponse, DataError> =
        apiCall { authService.loginWithTestUser() }

    override suspend fun getUserInfo(): Result<UserResponse, DataError> =
        apiCall { userService.getUserInfo() }

    override suspend fun reissue(refreshToken: String): Result<AuthResponse, DataError> =
        apiCall { authService.reissue(RefreshTokenRequest(refreshToken)) }

    override suspend fun syncInvitations(invitationIds: List<Long>): Result<Unit, DataError> =
        apiCall { invitationService.syncInvitations(invitationIds) }.map { Unit }
}
