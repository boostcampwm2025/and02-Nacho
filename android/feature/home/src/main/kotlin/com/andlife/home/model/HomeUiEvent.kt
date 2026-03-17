package com.andlife.home.model

import com.andlife.model.common.ReportReason
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

    data class UpdateMediaPlayState(
        val isPlaying: Boolean
    ) : HomeUiEvent

    data object DismissLoginDialog : HomeUiEvent

    data class ShowReport(
        val targetId: Long
    ) : HomeUiEvent

     data object DismissReport : HomeUiEvent

     data class SubmitReport(
         val reason: ReportReason,
         val description: String?
     ) : HomeUiEvent
}
