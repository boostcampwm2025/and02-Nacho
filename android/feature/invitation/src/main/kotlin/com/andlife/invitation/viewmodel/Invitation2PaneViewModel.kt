package com.andlife.invitation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.andlife.invitation.Invitation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class Invitation2PaneViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val route = savedStateHandle.toRoute<Invitation>()

    private val _uiState: MutableStateFlow<Invitation2PaneUiState> = MutableStateFlow(
        Invitation2PaneUiState(
            selectedInvitationId = route.initialInvitationId,
            isFromDeelLink = route.isFromDeepLink
        )
    )
    val uiState = _uiState.asStateFlow()

    fun onInvitationClick(invitationId: Long?) {
        _uiState.update { it.copy(selectedInvitationId = invitationId) }
    }
}

data class Invitation2PaneUiState(
    val selectedInvitationId: Long? = null,
    val isFromDeelLink: Boolean = false,
)
