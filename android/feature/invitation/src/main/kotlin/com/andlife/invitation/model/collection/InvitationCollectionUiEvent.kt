package com.andlife.invitation.model.collection

import com.andlife.ui.base.BaseUiEvent

sealed interface InvitationCollectionUiEvent : BaseUiEvent {
    data class OpenStory(
        val index: Int,
    ) : InvitationCollectionUiEvent

    data object CloseStory : InvitationCollectionUiEvent

    data class PageChanged(
        val index: Int,
    ) : InvitationCollectionUiEvent

    data object ToggleExpand : InvitationCollectionUiEvent

    data object DownloadMedia : InvitationCollectionUiEvent

    data object DisableNetworkDialogPermanently : InvitationCollectionUiEvent
}
