package com.andlife.invitation.model

import com.andlife.ui.base.BaseUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class InvitationUiState(
    val invitationIds: ImmutableList<Long> = persistentListOf()
) : BaseUiState
