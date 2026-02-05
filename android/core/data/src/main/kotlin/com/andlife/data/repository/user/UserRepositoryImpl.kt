package com.andlife.data.repository.user

import com.andlife.data.datasource.remote.user.UserRemoteDataSource
import com.andlife.data.repository.user.mapper.toDomain
import com.andlife.database.InvitationDatabase
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
    private val authStateManager: AuthStateManager,
    private val invitationDatabase: InvitationDatabase
) : UserRepository {
    override suspend fun login(accessToken: String): Result<Unit, DataError> {
        return userRemoteDataSource.login(AuthRequest(accessToken))
            .map { authResponse ->
                clearInvitationCache()
                authStateManager.setAuthenticated(authResponse.user.toDomain())
                saveToken(authResponse.accessToken, authResponse.refreshToken)
                syncGuestInvitations()
            }
    }

    override suspend fun loginWithTestUser(): Result<Unit, DataError> {
        return userRemoteDataSource.loginWithTestUser()
            .map {  authResponse ->
                clearInvitationCache()
                authStateManager.setAuthenticated(authResponse.user.toDomain())
                saveToken(authResponse.accessToken, authResponse.refreshToken)
                syncGuestInvitations()
            }
    }

    private suspend fun syncGuestInvitations(): Result<Unit, DataError> {
        val authState = authStateManager.authState.value

        val invitationIds = userStorage.getInvitationIds()
        val idsWithoutSample = invitationIds.filter { it != UserStorage.SAMPLE_INVITATION_ID }

        if (idsWithoutSample.isEmpty() && authState !is AuthState.Authenticated) return Result.Success(Unit)

        return userRemoteDataSource.syncInvitations(invitationIds)
            .map { userStorage.clearGuestData() }
    }

    override suspend fun guestLogin(): Result<Unit, InvitationError> {
        return runResultCatching {
            clearInvitationCache()
            userStorage.setWasLoggedIn(true)
            authStateManager.setGuest()
        }
    }

    override suspend fun initializeAuth(): Result<AuthState, DataError> {
        if (userStorage.isFirstLaunch()) {
            userStorage.addInvitationId(UserStorage.SAMPLE_INVITATION_ID)
            userStorage.setFirstLaunchDone()
        }

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
        authStateManager.setLoading()
        userStorage.clearTokens()
        userStorage.setWasLoggedIn(false)
        clearInvitationCache()
    }

    private suspend fun clearInvitationCache() {
        invitationDatabase.invitationSummaryDao().clearAll()
        invitationDatabase.upcomingInvitationDao().clearAll()
    }

    override suspend fun getUserInfo(): AuthState {
        return authStateManager.authState.value
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

    override suspend fun isWifiDialogDismissed(): Boolean = userStorage.isWifiDialogDismissed()

    override suspend fun setWifiDialogDismissed() = userStorage.setWifiDialogDismissed()

    override suspend fun isFirstDownloadDone(): Boolean = userStorage.isFirstDownloadDone()

    override suspend fun setFirstDownloadDone() = userStorage.setFirstDownloadDone()
}
