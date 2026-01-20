package com.andlife.invitation.model

import com.andlife.ui.base.BaseUiState

data class InvitationUiState(
    val invitationIds: List<Long> = emptyList()
) : BaseUiState
