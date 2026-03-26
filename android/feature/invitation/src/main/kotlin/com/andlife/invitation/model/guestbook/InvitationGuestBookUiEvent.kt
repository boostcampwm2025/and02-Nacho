package com.andlife.invitation.model.guestbook

import androidx.compose.ui.geometry.Rect
import com.andlife.model.common.ReportReason
import com.andlife.model.guestbook.GuestBookUiModel
import com.andlife.ui.base.BaseUiEvent
import com.andlife.ui.component.invitation.SelectedMedia

sealed interface InvitationGuestBookUiEvent : BaseUiEvent {
    data class UpdateSelectedMedias(
        val medias: List<SelectedMedia>,
    ) : InvitationGuestBookUiEvent

    data class UpdateTextContent(
        val textContent: String,
    ) : InvitationGuestBookUiEvent

    data class RemoveMedia(
        val media: SelectedMedia,
    ) : InvitationGuestBookUiEvent

    data object UploadMedias : InvitationGuestBookUiEvent

    data object ClickCamera : InvitationGuestBookUiEvent

    data object ClickMicrophone : InvitationGuestBookUiEvent

    data object ClearError : InvitationGuestBookUiEvent

    data class ClickVisualMedia(
        val url: String,
    ) : InvitationGuestBookUiEvent

    data class ClickAudioMedia(
        val url: String,
    ) : InvitationGuestBookUiEvent

    data class ClickVideoPlayButton(
        val url: String,
        val itemId: Long,
    ) : InvitationGuestBookUiEvent

    data class ClickEditMenu(
        val guestBook: GuestBookUiModel,
    ) : InvitationGuestBookUiEvent

    data object CancelEdit : InvitationGuestBookUiEvent

    data class ClickDeleteMenu(
        val guestBookId: Long,
    ) : InvitationGuestBookUiEvent

    data class UpdateMediaPlayState(
        val isPlaying: Boolean
    ) : InvitationGuestBookUiEvent

    data object Refresh : InvitationGuestBookUiEvent

    data object CheckLogin : InvitationGuestBookUiEvent

    data object DismissLoginDialog : InvitationGuestBookUiEvent

    data class ShowReport(
        val targetId: Long
    ) : InvitationGuestBookUiEvent

    data object DismissReport : InvitationGuestBookUiEvent

    data class SubmitReport(
        val reason: ReportReason,
        val description: String?
    ) : InvitationGuestBookUiEvent

    data class ShowFullscreenVideo(
        val videoUrl: String,
        val thumbnailUrl: String?,
        val startBounds: Rect,
    ) : InvitationGuestBookUiEvent

    data object DismissFullscreenVideo : InvitationGuestBookUiEvent

    data object ToggleVideoMute : InvitationGuestBookUiEvent
}
