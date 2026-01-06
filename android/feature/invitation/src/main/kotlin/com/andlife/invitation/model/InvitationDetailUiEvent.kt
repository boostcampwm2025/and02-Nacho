package com.andlife.invitation.model

import com.andlife.ui.base.BaseUiEvent

sealed interface InvitationDetailUiEvent : BaseUiEvent {
    data object ClickBack : InvitationDetailUiEvent
}
