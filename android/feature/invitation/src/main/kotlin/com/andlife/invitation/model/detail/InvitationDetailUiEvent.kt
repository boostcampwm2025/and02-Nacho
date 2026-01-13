package com.andlife.invitation.model.detail

import com.andlife.ui.base.BaseUiEvent
import com.andlife.ui.component.invitation.SelectedMedia

sealed interface InvitationDetailUiEvent : BaseUiEvent {
    data class UpdateSelectedMedias(
        val medias: List<SelectedMedia>,
    ) : InvitationDetailUiEvent

    data class RemoveMedia(
        val media: SelectedMedia,
    ) : InvitationDetailUiEvent

    data object UploadMedias : InvitationDetailUiEvent

    data object ClearError : InvitationDetailUiEvent
}
