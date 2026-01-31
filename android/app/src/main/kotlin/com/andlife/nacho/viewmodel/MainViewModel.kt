package com.andlife.nacho.viewmodel

import android.content.Intent
import androidx.lifecycle.viewModelScope
import com.andlife.deeplink.DeepLinkConfig
import com.andlife.deeplink.DeepLinkManager
import com.andlife.domain.model.auth.AuthEvent
import com.andlife.domain.model.auth.AuthState
import com.andlife.domain.repository.auth.AuthStateManager
import com.andlife.domain.repository.user.UserRepository
import com.andlife.domain.util.onFailure
import com.andlife.domain.util.onSuccess
import com.andlife.home.Home
import com.andlife.login.Login
import com.andlife.nacho.model.MainSideEffect
import com.andlife.nacho.model.MainUiEvent
import com.andlife.nacho.model.MainUiState
import com.andlife.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val deepLinkManager: DeepLinkManager,
    private val authStateManager: AuthStateManager,
    private val userRepository: UserRepository
) : BaseViewModel<MainUiState, MainUiEvent, MainSideEffect>(
    initialState = MainUiState(),
) {
    private var lastProcessedId: String? = null

    override val uiState: StateFlow<MainUiState> = mutableUiState
        .onStart {
            observeAuthEvent()
            observeDeferredDeepLink()
            loadUserInfo()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = MainUiState()
        )

    override fun onEvent(event: MainUiEvent) {
        // No events yet
    }

    private fun observeAuthEvent() {
        authStateManager.authEvent
            .onEach { event ->
                when (event) {
                    AuthEvent.NavigateToHome -> sendEffect(MainSideEffect.NavigateToHome)
                    AuthEvent.NavigateToLogin -> sendEffect(MainSideEffect.NavigateToLogin)
                }
            }.launchIn(viewModelScope)
    }

    private fun observeDeferredDeepLink() {
        deepLinkManager.deferredDeepLinkId
            .filterNotNull()
            .filter { it != lastProcessedId }
            .onEach { invitationId ->
                lastProcessedId = invitationId
                invitationId.toLongOrNull()?.let { id ->
                    sendEffect(MainSideEffect.NavigateToDetail(id))
                    deepLinkManager.clearInvitationId()
                }
            }.launchIn(viewModelScope)
    }

    private fun loadUserInfo() {
        viewModelScope.launch {
            userRepository.initializeAuth()
                .onSuccess { authState ->
                    when (authState) {
                        AuthState.Loading -> {
                            updateState { copy(isSplash = false, startDestination = Login::class) }
                        }
                        else -> {
                            updateState { copy(isSplash = false, startDestination = Home::class) }
                        }
                    }
                }
                .onFailure { error, msg ->
                    updateState { copy(isSplash = false, startDestination = Login::class) }
                }
        }
    }

    fun handleDeepLink(intent: Intent?) {
        val data = intent?.data ?: return

        val inviteId = data.getQueryParameter(DeepLinkConfig.KAKAO_PARAM_INVITE_ID)
            ?: data.getQueryParameter(DeepLinkConfig.AF_DEEP_LINK_SUB1)

        if (inviteId != null) {
            viewModelScope.launch {
                if (inviteId != lastProcessedId) {
                    deepLinkManager.emitInvitationId(inviteId)
                }
            }
        }
    }
}
