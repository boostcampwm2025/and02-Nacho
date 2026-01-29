package com.andlife.data.auth

import com.andlife.datastore.UserStorage
import com.andlife.domain.model.auth.AuthEvent
import com.andlife.domain.model.auth.AuthState
import com.andlife.domain.model.auth.User
import com.andlife.domain.repository.auth.AuthStateManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthStateManagerImpl @Inject constructor(
    private val userStorage: UserStorage
) : AuthStateManager {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _authEvent = Channel<AuthEvent>(Channel.BUFFERED)
    override val authEvent = _authEvent.receiveAsFlow()

    override suspend fun setAuthenticated(user: User) {
        _authState.update { AuthState.Authenticated(user) }
    }

    override suspend fun setGuest() {
        _authState.update { AuthState.Guest }
        userStorage.setWasLoggedIn(true)
    }

    override suspend fun setLoading() {
        _authState.update { AuthState.Loading }
    }

    override suspend fun navigateToLogin() {
        _authEvent.send(AuthEvent.NavigateToLogin)
    }

    override suspend fun navigateToHome() {
        _authEvent.send(AuthEvent.NavigateToHome)
    }
}
