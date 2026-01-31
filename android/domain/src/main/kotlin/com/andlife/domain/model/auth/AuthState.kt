package com.andlife.domain.model.auth

sealed interface AuthState {
    data class Authenticated(val user: User): AuthState

    data object Guest: AuthState

    data object Loading: AuthState
}

sealed interface AuthEvent {
    data object NavigateToLogin : AuthEvent
    data object NavigateToHome : AuthEvent
}
