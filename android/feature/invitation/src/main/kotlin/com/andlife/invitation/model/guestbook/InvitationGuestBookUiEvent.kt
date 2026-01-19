package com.andlife.invitation.model.guestbook

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

    data object ClearError : InvitationGuestBookUiEvent
}
