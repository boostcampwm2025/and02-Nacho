package com.andlife.home.model

import com.andlife.ui.base.BaseUiEvent

sealed interface HomeUiEvent : BaseUiEvent {

    data class ClickUpcomingInvitation(
        val invitationId: Long,
        val hostId: Long,
    ) : HomeUiEvent

    data class ClickInvitationTitle(
        val invitationId: Long,
        val hostId: Long,
    ) : HomeUiEvent

    data class ClickVisualMedia(
        val url: String,
    ) : HomeUiEvent

    data class ClickAudioMedia(
        val url: String,
    ) : HomeUiEvent

    data object ClickSetting : HomeUiEvent

    data object ClickCreate : HomeUiEvent

    data object Refresh : HomeUiEvent

    data object Retry : HomeUiEvent
}
