package com.andlife.data.repository.user

import com.andlife.data.datasource.remote.user.UserRemoteDataSource
import com.andlife.datastore.UserStorage
import com.andlife.domain.error.DataError
import com.andlife.domain.repository.user.UserRepository
import com.andlife.domain.util.Result
import com.andlife.domain.util.map
import com.andlife.network.model.auth.AuthRequest
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
    private val userStorage: UserStorage,
    private val userRemoteDataSource: UserRemoteDataSource
) : UserRepository {
    override fun getUserId(): Long? = userStorage.getUserId()

    override suspend fun saveUserId(userId: Long) = userStorage.saveUserId(userId)

    override suspend fun clearUserSession() = userStorage.clearUserSession()
    override suspend fun login(accessToken: String): Result<Unit, DataError> {
         return userRemoteDataSource.login(AuthRequest(accessToken)).map { Unit }
    }
}
