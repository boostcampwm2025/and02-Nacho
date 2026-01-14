package com.andlife.myinvitation.model

import com.andlife.ui.base.BaseUiEvent

sealed interface MyInvitationDetailUiEvent : BaseUiEvent {
    data object ClickBack : MyInvitationDetailUiEvent

    data object ClickShare : MyInvitationDetailUiEvent
}
