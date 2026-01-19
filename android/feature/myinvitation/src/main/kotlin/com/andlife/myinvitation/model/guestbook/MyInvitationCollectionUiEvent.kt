package com.andlife.myinvitation.model.guestbook

import com.andlife.ui.base.BaseUiEvent

sealed interface MyInvitationCollectionUiEvent : BaseUiEvent {
    data class OpenStory(
        val index: Int,
    ) : MyInvitationCollectionUiEvent

    data object CloseStory : MyInvitationCollectionUiEvent

    data class PageChanged(
        val index: Int,
    ) : MyInvitationCollectionUiEvent

    data object ToggleExpand : MyInvitationCollectionUiEvent
}
