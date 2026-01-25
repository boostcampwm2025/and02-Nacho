package com.andlife.invitation_card.model.creaetbyinvitation

import com.andlife.ui.base.BaseUiEvent

sealed interface CreateByInvitationUiEvent : BaseUiEvent {
    data object ClickSave : CreateByInvitationUiEvent
    data object ClickBack : CreateByInvitationUiEvent
}
