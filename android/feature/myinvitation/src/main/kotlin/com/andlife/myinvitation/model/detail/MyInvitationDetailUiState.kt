package com.andlife.myinvitation.model.detail

import com.andlife.model.invitation.InvitationContentsUiModel
import com.andlife.ui.base.BaseUiState

data class MyInvitationDetailUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val hasThanksCard: Boolean = false,
    val invitationContentsUiModel: InvitationContentsUiModel = InvitationContentsUiModel(),
) : BaseUiState
