package com.andlife.invitation.model.guestbook

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

    data class ClickInvitationTitle(
        val invitationId: Long,
    ) : InvitationGuestBookUiEvent

    data class ClickGuestBookMenu(
        val guestBookId: Long,
    ) : InvitationGuestBookUiEvent

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
}
