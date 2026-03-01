package com.andlife.login.viewmodel

import androidx.lifecycle.viewModelScope
import com.andlife.domain.repository.auth.AuthStateManager
import com.andlife.domain.repository.user.UserRepository
import com.andlife.domain.util.AnalyticsEvent
import com.andlife.domain.util.AnalyticsLogger
import com.andlife.domain.util.Button
import com.andlife.domain.util.CrashlyticsLogger
import com.andlife.domain.util.LoginMethod
import com.andlife.domain.util.Screen
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
    private val authStateManager: AuthStateManager,
    private val analyticsLogger: AnalyticsLogger,
    private val crashLogger: CrashlyticsLogger,
) : BaseViewModel<LoginUiState, LoginUiEvent, LoginSideEffect>(LoginUiState()) {

    override val uiState: StateFlow<LoginUiState> = mutableUiState.asStateFlow()
    override fun onEvent(event: LoginUiEvent) {
        when (event) {
            LoginUiEvent.GuestLogin -> {
                analyticsLogger.logEvent(AnalyticsEvent.ButtonClick(Screen.LOGIN, Button.GUEST_LOGIN))
                guest()
            }
            is LoginUiEvent.SocialLoginSuccess -> {
                analyticsLogger.logEvent(AnalyticsEvent.ButtonClick(Screen.LOGIN, Button.KAKAO_LOGIN))
                login(event.accessToken)
            }
            LoginUiEvent.TestLogin -> testLogin()
        }
    }

    private fun login(accessToken: String) {
        viewModelScope.launch {
            updateState { copy(true) }
            userRepository.login(accessToken)
                .onFailure { error, msg ->
                    crashLogger.recordException("$error: $msg")
                    sendEffect(LoginSideEffect.FailSocialLogin)
                }
                .onSuccess {
                    analyticsLogger.logEvent(AnalyticsEvent.Login(LoginMethod.KAKAO))
                    authStateManager.navigateToHome()
                }
            updateState { copy(false) }
        }
    }

    private fun testLogin() {
        viewModelScope.launch {
            updateState { copy(true) }
            userRepository.loginWithTestUser()
                .onFailure { error, msg ->
                    sendEffect(LoginSideEffect.FailTestLogin)
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
                    crashLogger.recordException("$error: $msg")
                    sendEffect(LoginSideEffect.FailGuestLogin)
                }
                .onSuccess {
                    analyticsLogger.logEvent(AnalyticsEvent.Login(LoginMethod.GUEST))
                    authStateManager.navigateToHome()
                }
            updateState { copy(false) }
        }
    }
}
