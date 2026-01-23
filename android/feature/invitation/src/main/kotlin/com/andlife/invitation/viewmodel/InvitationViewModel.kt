package com.andlife.invitation.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.andlife.domain.repository.invitation.InvitationRepository
import com.andlife.domain.repository.user.UserRepository
import com.andlife.domain.util.onFailure
import com.andlife.domain.util.onSuccess
import com.andlife.invitation.model.InvitationSideEffect
import com.andlife.invitation.model.InvitationUiEvent
import com.andlife.invitation.model.InvitationUiState
import com.andlife.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvitationViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val invitationRepository: InvitationRepository
) : BaseViewModel<InvitationUiState, InvitationUiEvent, InvitationSideEffect>(
    initialState = InvitationUiState()
) {
    override val uiState: StateFlow<InvitationUiState> =
        mutableUiState
            .onStart {
                loadInvitations()
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = InvitationUiState()
            )

    override fun onEvent(event: InvitationUiEvent) {
        TODO("Not yet implemented")
    }

    private fun loadInvitations() {
        viewModelScope.launch {
            // 로그인
            userRepository.saveUserId(1L)

            invitationRepository.getParticipantInvitations()
                .onSuccess { ids ->
                    updateState { copy(invitationIds = ids.toPersistentList()) }
                }
                .onFailure { error, msg ->
                    Log.e("InvitationViewModel", "Error loading invitations: $error")
                }
        }
    }


}
