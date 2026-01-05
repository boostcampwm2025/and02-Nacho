package com.andlife.invitation.model

import com.andlife.domain.model.GuestBookMedia
import com.andlife.ui.base.BaseUiState

data class InvitationUiState(
    val isLoading: Boolean = false,
    val mediaItems: List<GuestBookMedia> = emptyList(),
    val errorMessage: String? = null
): BaseUiState
