package com.andlife.myinvitation.model.detail

import com.andlife.ui.base.BaseUiEvent
import kotlinx.collections.immutable.ImmutableList

sealed interface MyInvitationDetailUiEvent : BaseUiEvent {
    data object ClickBack : MyInvitationDetailUiEvent

    data object ClickThanksCard : MyInvitationDetailUiEvent

    data object ClickShare : MyInvitationDetailUiEvent

    data object ClickEdit : MyInvitationDetailUiEvent

    data object ClickDelete : MyInvitationDetailUiEvent

    data object CreateThanksCard : MyInvitationDetailUiEvent

    data object ClickEditCard : MyInvitationDetailUiEvent

    data object ClickCreateCard : MyInvitationDetailUiEvent

    data class ClickImage(
        val imageList: ImmutableList<String>,
        val index: Int,
    ) : MyInvitationDetailUiEvent

    data object MapError : MyInvitationDetailUiEvent

    data object RetryLoad : MyInvitationDetailUiEvent
}
