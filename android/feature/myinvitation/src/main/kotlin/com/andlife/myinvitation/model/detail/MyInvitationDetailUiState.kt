package com.andlife.myinvitation.model.detail

import com.andlife.model.invitation.InvitationContentsUiModel
import com.andlife.ui.base.BaseUiState

data class MyInvitationDetailUiState(
    val id: Long = 0L,
    val title: String = "",
    val hasThanksCard: Boolean = false,
    val invitationContentsUiModel: InvitationContentsUiModel = InvitationContentsUiModel(),
    val isLoading: Boolean = false,
) : BaseUiState
