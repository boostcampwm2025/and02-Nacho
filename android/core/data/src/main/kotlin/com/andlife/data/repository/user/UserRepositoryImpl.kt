package com.andlife.data.repository.user

import com.andlife.data.datasource.remote.user.UserRemoteDataSource
import com.andlife.datastore.UserStorage
import com.andlife.domain.repository.user.UserRepository
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
    private val userStorage: UserStorage,
    private val userRemoteDataSource: UserRemoteDataSource
) : UserRepository {
    override fun getUserId(): Long? = userStorage.getUserId()

    override suspend fun saveUserId(userId: Long) = userStorage.saveUserId(userId)

    override suspend fun clearUserSession() = userStorage.clearUserSession()
}
