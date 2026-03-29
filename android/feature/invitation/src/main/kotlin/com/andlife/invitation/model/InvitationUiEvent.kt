package com.andlife.invitation.model

import com.andlife.domain.model.invitation.SortDirection
import com.andlife.model.common.ReportReason
import com.andlife.ui.base.BaseUiEvent

sealed interface InvitationUiEvent : BaseUiEvent {
    data class SelectTab(val index: Int) : InvitationUiEvent
    data class ClickInvitation(val id: Long) : InvitationUiEvent
    data class ChangeSort(val isUpcoming: Boolean, val newSort: SortDirection) : InvitationUiEvent
    data class ClickLeaveInvitation(val id: Long) : InvitationUiEvent
    data class ShowReport(val invitationId: Long) : InvitationUiEvent
    data object DismissReport : InvitationUiEvent
    data class SubmitReport(val reason: ReportReason, val description: String?) : InvitationUiEvent
    data object DismissLoginDialog : InvitationUiEvent
}
