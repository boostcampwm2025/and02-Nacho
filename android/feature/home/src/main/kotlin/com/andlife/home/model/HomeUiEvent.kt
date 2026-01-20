package com.andlife.home.model

import com.andlife.ui.base.BaseUiEvent

sealed interface HomeUiEvent : BaseUiEvent {
    data class ClickInvitationTitle(
        val invitationId: Long,
    ) : HomeUiEvent

    data class ClickVisualMedia(
        val url: String,
    ) : HomeUiEvent

    data class ClickAudioMedia(
        val url: String,
    ) : HomeUiEvent

    data object ClickSetting : HomeUiEvent

    data object RetryLoad : HomeUiEvent

    data class ClickUpcomingInvitation(
        val invitationId: Long,
    ) : HomeUiEvent
}
