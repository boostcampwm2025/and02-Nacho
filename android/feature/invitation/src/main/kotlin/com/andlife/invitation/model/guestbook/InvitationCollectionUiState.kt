package com.andlife.invitation.model.guestbook

import com.andlife.ui.base.BaseUiState

data class InvitationCollectionUiState(
    val isLoading: Boolean = false,
    val mediaItems: List<InvitationCollectionUiModel> = emptyList(),
    val errorMessage: String? = null
): BaseUiState
