package com.andlife.invitation.model

import com.andlife.ui.base.BaseUiEvent

sealed interface InvitationUiEvent : BaseUiEvent {
    object Refresh : InvitationUiEvent
    data class SelectTab(val index: Int) : InvitationUiEvent
    data class ClickInvitation(val id: Long) : InvitationUiEvent
}
