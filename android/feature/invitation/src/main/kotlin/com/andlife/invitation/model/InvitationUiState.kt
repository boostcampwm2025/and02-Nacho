package com.andlife.invitation.model

import com.andlife.invitation.model.guestbook.InvitationMediaUiModel
import com.andlife.ui.base.BaseUiState

data class InvitationUiState(
    val isLoading: Boolean = false,
    val mediaItems: List<InvitationMediaUiModel> = emptyList(),
    val errorMessage: String? = null
): BaseUiState
