package com.andlife.home.model.home

import com.andlife.ui.base.BaseUiEvent

sealed interface HomeUiEvent : BaseUiEvent {

    data class ClickUpcomingInvitation(
        val invitationId: Long,
        val isOwner: Boolean,
    ) : HomeUiEvent

    data class ClickInvitationTitle(
        val invitationId: Long,
        val isOwner: Boolean,
    ) : HomeUiEvent

    data class ClickVisualMedia(
        val url: String,
    ) : HomeUiEvent

    data class ClickAudioMedia(
        val url: String,
    ) : HomeUiEvent

    data class ClickVideoPlayButton(
        val url: String,
        val itemId: Long,
    ) : HomeUiEvent

    data object ClickSetting : HomeUiEvent

    data object ClickCreate : HomeUiEvent

    data object Refresh : HomeUiEvent

    data class UpdateMediaPlayState(
        val isPlaying: Boolean
    ) : HomeUiEvent
}
