package com.andlife.nacho.viewmodel

import android.content.Intent
import androidx.lifecycle.viewModelScope
import com.andlife.deeplink.DeepLinkManager
import com.andlife.domain.repository.user.UserRepository
import com.andlife.nacho.model.MainSideEffect
import com.andlife.nacho.model.MainUiEvent
import com.andlife.nacho.model.MainUiState
import com.andlife.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val deepLinkManager: DeepLinkManager,
    private val userRepository: UserRepository,
) : BaseViewModel<MainUiState, MainUiEvent, MainSideEffect>(
    initialState = MainUiState,
) {
    private var lastProcessedId: String? = null

    override val uiState: StateFlow<MainUiState> = mutableUiState.asStateFlow()

    init {
        viewModelScope.launch {
            // TODO: 테스트 사용자 1L
            userRepository.saveUserId(1L)
        }
    }

    override fun onEvent(event: MainUiEvent) {
        // No events yet
    }

    private val deferredDeepLinkJob =
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

    fun handleDeepLink(intent: Intent?) {
        val data = intent?.data ?: return
        sendEffect(MainSideEffect.HandleDeepLink(intent))
    }
}
