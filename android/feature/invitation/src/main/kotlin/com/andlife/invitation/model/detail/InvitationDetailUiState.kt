package com.andlife.invitation.model.detail

import com.andlife.ui.base.BaseUiState

data class InvitationDetailUiState(
    val id: Long = 0L,
    val isLoading: Boolean = false,
) : BaseUiState
