package com.andlife.model.invitation

import com.andlife.domain.model.invitation.Announcement
import com.andlife.domain.model.invitation.InvitationCard
import com.andlife.domain.model.invitation.Invitation
import com.andlife.model.editor.toUiModel
import kotlinx.collections.immutable.toImmutableList


fun Invitation.toContentsUiModel(): InvitationContentsUiModel {
    return InvitationContentsUiModel(
        title = title,
        hostInfo = HostInfo(
            name = displayHostName,
            profileUrl = hostProfileUrl,
        ),
        imageList = thumbnailUrls.toImmutableList(),
        dateTime = DateTimeInfo(
            date = invitationDate,
            startTime = TimeUiModel(
                hour = startTime.hour,
                min = startTime.minute,
            ),
        ),
        location = LocationInfo(
            name = placename,
            address = address,
            guide = locationGuide,
            latLng = LatLngUiModel(
                latitude = latitude,
                longitude = longitude,
            ),
        ),
        announcement = announcements
            .sortedBy { it.displayOrder }
            .map { it.toUiModel() }
            .toImmutableList(),
        invitationCard = invitationCard?.toUiModel(),
        thanksCard = thanksCard?.toUiModel()
    )
}

fun Announcement.toUiModel(): AnnouncementUiModel {
    return AnnouncementUiModel(
        id = id,
        title = title,
        content = content,
    )
}

fun InvitationCard.toUiModel(): InvitationCardUiModel {
    return InvitationCardUiModel(
        id = id,
        invitationId = invitationId,
        card = card.toUiModel(),
    )
}
