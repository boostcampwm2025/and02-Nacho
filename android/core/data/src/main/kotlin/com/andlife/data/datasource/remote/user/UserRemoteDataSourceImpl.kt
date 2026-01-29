package com.andlife.data.datasource.remote.user

import com.andlife.data.util.apiCall
import com.andlife.domain.error.DataError
import com.andlife.domain.util.Result
import com.andlife.network.api.auth.AuthService
import com.andlife.network.model.auth.AuthRequest
import com.andlife.network.model.auth.AuthResponse
import javax.inject.Inject

class UserRemoteDataSourceImpl @Inject constructor(
    private val authService: AuthService
) : UserRemoteDataSource {
    override suspend fun login(request: AuthRequest): Result<AuthResponse, DataError> =
        apiCall { authService.login(request) }
}
