package com.andlife.invitation_edit.model.create

import com.andlife.domain.model.card.NachoCard
import com.andlife.domain.model.invitation.CreateAnnouncementParam
import com.andlife.domain.model.invitation.CreateInvitationParam
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

fun CreateInvitationUiModel.toCreateParam(
    thumbnails: List<String>,
    date: LocalDate,
    startTime: LocalTime,
    endTime: LocalTime?,
    invitationCard: NachoCard?,
): CreateInvitationParam {
    return CreateInvitationParam(
        title = title,
        displayHostName = author,
        thumbnailUrls = thumbnails,
        invitationDate = date,
        startTime = startTime,
        endTime = endTime,
        placename = placeName,
        address = placeAddress,
        latitude = lat,
        longitude = lng,
        locationGuide = placeGuide.ifBlank { null },
        invitationCard = invitationCard,
        announcements = announcement.mapIndexed { index, announcementUiModel ->
            announcementUiModel.toCreateParam(index)
        }
    )
}

fun AnnouncementUiModel.toCreateParam(index: Int): CreateAnnouncementParam {
    return CreateAnnouncementParam(
        title = title,
        content = content,
        displayOrder = index
    )
}
