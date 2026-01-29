package com.andlife.data.datasource.remote.user

import com.andlife.domain.error.DataError
import com.andlife.domain.util.Result
import com.andlife.network.model.auth.AuthRequest
import com.andlife.network.model.auth.AuthResponse

interface UserRemoteDataSource {
    suspend fun login(request: AuthRequest): Result<AuthResponse, DataError>
}
