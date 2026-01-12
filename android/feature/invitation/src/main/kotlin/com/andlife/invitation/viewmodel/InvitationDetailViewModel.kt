package com.andlife.invitation.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.andlife.invitation.model.detail.InvitationDetailSideEffect
import com.andlife.invitation.model.detail.InvitationDetailUiEvent
import com.andlife.invitation.model.detail.InvitationDetailUiState
import com.andlife.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class InvitationDetailViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
    ) : BaseViewModel<InvitationDetailUiState, InvitationDetailUiEvent, InvitationDetailSideEffect>(
            InvitationDetailUiState(),
        ) {
        val invitationId: Long = checkNotNull(savedStateHandle["id"])

        override val uiState: StateFlow<InvitationDetailUiState>
            get() = TODO("Not yet implemented")

        override fun onEvent(event: InvitationDetailUiEvent) {
            TODO("Not yet implemented")
        }
    }
