package com.andlife.myinvitation.model.guestbook

import com.andlife.model.guestbook.GuestBookUiModel
import com.andlife.ui.base.BaseUiEvent
import com.andlife.ui.component.invitation.SelectedMedia

sealed interface MyInvitationGuestBookUiEvent : BaseUiEvent {
    data class UpdateSelectedMedias(
        val medias: List<SelectedMedia>,
        val exceededAvailableBytes: Boolean,
        val exceededAvailableSlots: Boolean,
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
}
