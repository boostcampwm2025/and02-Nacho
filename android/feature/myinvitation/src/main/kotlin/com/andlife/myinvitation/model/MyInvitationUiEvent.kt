package com.andlife.myinvitation.model

import com.andlife.domain.model.invitation.SortDirection
import com.andlife.ui.base.BaseUiEvent

sealed interface MyInvitationUiEvent : BaseUiEvent {
    object Refresh : MyInvitationUiEvent
    data class SelectTab(val index: Int) : MyInvitationUiEvent
    data class ClickInvitation(val id: Long) : MyInvitationUiEvent
    data class ChangeSort(val isUpcoming: Boolean, val newSort: SortDirection) : MyInvitationUiEvent
}
