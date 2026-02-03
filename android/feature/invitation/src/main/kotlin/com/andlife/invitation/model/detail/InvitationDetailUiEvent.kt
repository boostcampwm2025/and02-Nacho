package com.andlife.invitation.model.detail

import com.andlife.ui.base.BaseUiEvent
import kotlinx.collections.immutable.ImmutableList

sealed interface InvitationDetailUiEvent : BaseUiEvent {
    data object ClickBack : InvitationDetailUiEvent

    data object ClickThanksCard : InvitationDetailUiEvent

    data object ClickLeaveInvitation : InvitationDetailUiEvent

    data class ClickImage(
        val imageList: ImmutableList<String>,
        val index: Int,
    ) : InvitationDetailUiEvent

    data object MapError : InvitationDetailUiEvent

    data object RetryLoad : InvitationDetailUiEvent
}
