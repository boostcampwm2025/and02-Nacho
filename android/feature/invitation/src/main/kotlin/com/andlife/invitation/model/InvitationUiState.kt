package com.andlife.invitation.model

import com.andlife.domain.model.invitation.InvitationSummary
import com.andlife.ui.base.BaseUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class InvitationUiState(
    val invitationSummaries: ImmutableList<InvitationSummary> = persistentListOf(),
) : BaseUiState
