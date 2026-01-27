package com.andlife.invitation.model

import com.andlife.domain.model.invitation.SortDirection
import com.andlife.ui.base.BaseUiEvent

sealed interface InvitationUiEvent : BaseUiEvent {
    data object Refresh : InvitationUiEvent
    data class SelectTab(val index: Int) : InvitationUiEvent
    data class ClickInvitation(val id: Long) : InvitationUiEvent
    data class ChangeSort(val isUpcoming: Boolean, val newSort: SortDirection) : InvitationUiEvent
}
