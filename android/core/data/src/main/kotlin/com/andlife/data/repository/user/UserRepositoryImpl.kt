package com.andlife.data.repository.user

import com.andlife.data.datasource.remote.user.UserRemoteDataSource
import com.andlife.data.repository.user.mapper.toDomain
import com.andlife.datastore.UserStorage
import com.andlife.domain.error.DataError
import com.andlife.domain.error.InvitationError
import com.andlife.domain.model.auth.AuthState
import com.andlife.domain.repository.auth.AuthStateManager
import com.andlife.domain.repository.user.UserRepository
import com.andlife.domain.util.Result
import com.andlife.domain.util.map
import com.andlife.domain.util.runResultCatching
import com.andlife.network.model.auth.AuthRequest
import java.io.IOException
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
    private val userStorage: UserStorage,
    private val userRemoteDataSource: UserRemoteDataSource,
    private val authStateManager: AuthStateManager
) : UserRepository {
    override fun getUserId(): Long? = userStorage.getUserId()

    override suspend fun saveUserId(userId: Long) = userStorage.saveUserId(userId)

    override suspend fun clearUserSession() = userStorage.clearUserSession()
    override suspend fun login(accessToken: String): Result<Unit, DataError> {
        return userRemoteDataSource.login(AuthRequest(accessToken))
            .map { authResponse ->
                authStateManager.setAuthenticated(authResponse.user.toDomain())
                saveToken(authResponse.accessToken, authResponse.refreshToken)
            }
    }

    override suspend fun guestLogin(): Result<Unit, InvitationError> {
        return runResultCatching {
            userStorage.setWasLoggedIn(true)
            authStateManager.setGuest()
        }
    }

    override suspend fun initializeAuth(): Result<AuthState, DataError> {
        val accessToken = userStorage.getAccessToken()

        if (accessToken == null) {
            val wasLoggedIn = userStorage.getWasLoggedIn()

            return if (wasLoggedIn) {
                authStateManager.setGuest()
                Result.Success(AuthState.Guest)
            } else {
                Result.Success(AuthState.Loading)
            }
        }

        return userRemoteDataSource.getUserInfo().map { userResponse ->
            val user = userResponse.toDomain()
            authStateManager.setAuthenticated(user)
            AuthState.Authenticated(user)
        }
    }

    override suspend fun logout() {
        TODO("Not yet implemented")
    }

    private suspend fun saveToken(accessToken: String, refreshToken: String): Result<Unit, DataError> {
        return try {
            userStorage.saveTokens(
                accessToken = accessToken,
                refreshToken = refreshToken
            )
            Result.Success(Unit)
        } catch (e: IOException) {
            Result.Error(DataError.Local.IOEXCEPTION)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }
}
