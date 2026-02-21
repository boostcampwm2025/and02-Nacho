package com.andlife.invitation_edit.model.form

import com.andlife.invitation_edit.model.address.AddressUiModel
import com.andlife.ui.base.BaseUiEvent
import kotlinx.datetime.LocalDate

sealed interface InvitationFormUiEvent : BaseUiEvent {
    data class UpdateTitle(
        val title: String,
    ) : InvitationFormUiEvent

    data class UpdateAuthor(
        val author: String,
    ) : InvitationFormUiEvent

    data class UpdateImageList(
        val imageList: List<String>,
    ) : InvitationFormUiEvent

    data class RemoveImage(
        val image: ThumbnailImageUiModel,
    ) : InvitationFormUiEvent

    data class UpdateDate(
        val date: LocalDate,
    ) : InvitationFormUiEvent

    data class UpdateStartTime(
        val hour: Int,
        val min: Int,
    ) : InvitationFormUiEvent

    data class UpdateEndTime(
        val hour: Int,
        val min: Int,
    ) : InvitationFormUiEvent

    data class UpdateAddress(
        val address: AddressUiModel,
    ) : InvitationFormUiEvent

    data class UpdatePlaceAddress(
        val placeAddress: String,
    ) : InvitationFormUiEvent

    data class UpdateAddressGuide(
        val addressGuide: String,
    ) : InvitationFormUiEvent

    data class UpdateAnnouncement(
        val title: String,
        val content: String,
    ) : InvitationFormUiEvent

    data class RemoveAnnouncement(
        val announcement: AnnouncementUiModel,
    ) : InvitationFormUiEvent

    data object OnClickBack : InvitationFormUiEvent

    data object OnClickSave : InvitationFormUiEvent

    data object OnClickPreview : InvitationFormUiEvent
}
