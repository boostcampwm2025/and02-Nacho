package com.andlife.login.viewmodel

import androidx.lifecycle.viewModelScope
import com.andlife.domain.repository.auth.AuthStateManager
import com.andlife.domain.repository.user.UserRepository
import com.andlife.domain.util.onFailure
import com.andlife.domain.util.onSuccess
import com.andlife.login.model.LoginSideEffect
import com.andlife.login.model.LoginUiEvent
import com.andlife.login.model.LoginUiState
import com.andlife.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authStateManager: AuthStateManager
) : BaseViewModel<LoginUiState, LoginUiEvent, LoginSideEffect>(LoginUiState()) {


    override val uiState: StateFlow<LoginUiState> = mutableUiState.asStateFlow()
    override fun onEvent(event: LoginUiEvent) {
        when (event) {
            LoginUiEvent.GuestLogin -> guest()
            is LoginUiEvent.SocialLoginSuccess -> login(event.accessToken)
        }
    }

    private fun login(accessToken: String) {
        viewModelScope.launch {
            updateState { copy(true) }
            userRepository.login(accessToken)
                .onFailure { error, msg ->
                    sendEffect(LoginSideEffect.FailSocialLogin)
                }
                .onSuccess {
                    authStateManager.navigateToHome()
                }
            updateState { copy(false) }
        }
    }

    private fun guest() {
        viewModelScope.launch {
            updateState { copy(true) }
            userRepository.guestLogin()
                .onFailure { error, msg ->
                    sendEffect(LoginSideEffect.FailGuestLogin)
                }
                .onSuccess {
                    authStateManager.navigateToHome()
                }
            updateState { copy(false) }
        }
    }
}
