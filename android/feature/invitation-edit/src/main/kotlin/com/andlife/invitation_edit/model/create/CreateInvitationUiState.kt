package com.andlife.invitation_edit.model.create

import com.andlife.ui.base.BaseUiState

data class CreateInvitationUiState(
    val createInvitationUiModel: CreateInvitationUiModel = CreateInvitationUiModel(),
    val isLoading: Boolean = false,
) : BaseUiState
