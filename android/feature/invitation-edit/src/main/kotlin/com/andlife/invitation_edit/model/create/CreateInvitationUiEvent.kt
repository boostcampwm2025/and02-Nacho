package com.andlife.invitation_edit.model.create

import com.andlife.invitation_edit.model.AddressUiModel
import com.andlife.ui.base.BaseUiEvent
import kotlinx.datetime.LocalDate

sealed interface CreateInvitationUiEvent : BaseUiEvent {
    data class UpdateTitle(
        val title: String,
    ) : CreateInvitationUiEvent

    data class UpdateAuthor(
        val author: String,
    ) : CreateInvitationUiEvent

    data class UpdateImageList(
        val imageList: List<String>,
    ) : CreateInvitationUiEvent

    data class RemoveImage(
        val image: ThumbnailImageUiModel,
    ) : CreateInvitationUiEvent

    data class UpdateDate(
        val date: LocalDate,
    ) : CreateInvitationUiEvent

    data class UpdateStartTime(
        val hour: Int,
        val min: Int,
    ) : CreateInvitationUiEvent

    data class UpdateEndTime(
        val hour: Int,
        val min: Int,
    ) : CreateInvitationUiEvent

    data class UpdateAddress(
        val address: AddressUiModel,
    ) : CreateInvitationUiEvent

    data class UpdatePlaceAddress(
        val placeAddress: String,
    ) : CreateInvitationUiEvent

    data class UpdateAddressGuide(
        val addressGuide: String,
    ) : CreateInvitationUiEvent

    data class UpdateAnnouncement(
        val title: String,
        val content: String,
    ) : CreateInvitationUiEvent

    data class RemoveAnnouncement(
        val announcement: AnnouncementUiModel,
    ) : CreateInvitationUiEvent

    data object OnClickBack : CreateInvitationUiEvent

    data object OnClickCreate : CreateInvitationUiEvent
}
