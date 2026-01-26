package com.andlife.invitation_edit.model.form

import com.andlife.domain.model.card.NachoCard
import com.andlife.domain.model.invitation.AnnouncementSaveParam
import com.andlife.domain.model.invitation.InvitationSaveParam
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

fun InvitationFormUiModel.toCreateParam(
    thumbnails: List<String>,
    date: LocalDate,
    startTime: LocalTime,
    endTime: LocalTime?,
    invitationCard: NachoCard?,
): InvitationSaveParam {
    return InvitationSaveParam(
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

fun AnnouncementUiModel.toCreateParam(index: Int): AnnouncementSaveParam {
    return AnnouncementSaveParam(
        title = title,
        content = content,
        displayOrder = index
    )
}
