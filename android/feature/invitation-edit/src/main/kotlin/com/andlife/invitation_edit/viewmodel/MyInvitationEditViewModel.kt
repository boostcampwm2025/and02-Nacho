package com.andlife.invitation_edit.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.andlife.domain.repository.invitation.InvitationRepository
import com.andlife.domain.util.MediaFileProvider
import com.andlife.domain.util.MediaUploader
import com.andlife.editor.util.CardConverter
import com.andlife.editor.util.CreateCardSession
import com.andlife.invitation_edit.model.create.CreateInvitationSideEffect
import com.andlife.invitation_edit.model.create.CreateInvitationUiEvent
import com.andlife.invitation_edit.model.create.CreateInvitationUiState
import com.andlife.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MyInvitationEditViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val createCardSession: CreateCardSession,
    private val mediaFileProvider: MediaFileProvider,
    private val mediaUploader: MediaUploader,
    private val editorConverter: CardConverter,
    private val invitationRepository: InvitationRepository,
) :
    BaseViewModel<CreateInvitationUiState, CreateInvitationUiEvent, CreateInvitationSideEffect>(
        CreateInvitationUiState(),
    ) {
    override val uiState: StateFlow<CreateInvitationUiState> = mutableUiState.asStateFlow()

    override fun onEvent(event: CreateInvitationUiEvent) {

    }
}
