package com.andlife.myinvitation.model.guestbook

import androidx.compose.ui.geometry.Rect
import com.andlife.model.common.ReportReason
import com.andlife.model.guestbook.GuestBookUiModel
import com.andlife.ui.base.BaseUiEvent
import com.andlife.ui.component.invitation.SelectedMedia

sealed interface MyInvitationGuestBookUiEvent : BaseUiEvent {
    data class UpdateSelectedMedias(
        val medias: List<SelectedMedia>,
    ) : MyInvitationGuestBookUiEvent

    data class UpdateTextContent(
        val textContent: String,
    ) : MyInvitationGuestBookUiEvent

    data class RemoveMedia(
        val media: SelectedMedia,
    ) : MyInvitationGuestBookUiEvent

    data object UploadMedias : MyInvitationGuestBookUiEvent

    data object ClickCamera : MyInvitationGuestBookUiEvent

    data object ClickMicrophone : MyInvitationGuestBookUiEvent

    data object ClearError : MyInvitationGuestBookUiEvent

    data class ClickVisualMedia(
        val url: String,
    ) : MyInvitationGuestBookUiEvent

    data class ClickAudioMedia(
        val url: String,
    ) : MyInvitationGuestBookUiEvent

    data class ClickVideoPlayButton(
        val url: String,
        val itemId: Long,
    ) : MyInvitationGuestBookUiEvent

    data class ClickEditMenu(
        val guestBook: GuestBookUiModel,
    ) : MyInvitationGuestBookUiEvent

    data object CancelEdit : MyInvitationGuestBookUiEvent

    data class ClickDeleteMenu(
        val guestBookId: Long,
    ) : MyInvitationGuestBookUiEvent

    data class UpdateMediaPlayState(
        val isPlaying: Boolean
    ) : MyInvitationGuestBookUiEvent

    data object Refresh : MyInvitationGuestBookUiEvent

    data object CheckLogin : MyInvitationGuestBookUiEvent

    data object DismissLoginDialog : MyInvitationGuestBookUiEvent

    data class ShowReport(
        val guestBookId: Long,
    ) : MyInvitationGuestBookUiEvent

    data object DismissReport : MyInvitationGuestBookUiEvent

    data class SubmitReport(
        val reason: ReportReason,
        val description: String?
    ) : MyInvitationGuestBookUiEvent

    data class ShowFullscreenVideo(
        val videoUrl: String,
        val thumbnailUrl: String?,
        val startBounds: Rect,
    ) : MyInvitationGuestBookUiEvent

    data object DismissFullscreenVideo : MyInvitationGuestBookUiEvent

    data object ToggleVideoMute : MyInvitationGuestBookUiEvent
}
