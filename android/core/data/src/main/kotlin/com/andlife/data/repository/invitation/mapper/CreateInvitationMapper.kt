package com.andlife.data.repository.invitation.mapper

import com.andlife.domain.model.card.NachoCard
import com.andlife.domain.model.invitation.AnnouncementSaveParam
import com.andlife.domain.model.invitation.InvitationSaveParam
import com.andlife.network.model.invitation.AnnouncementRequest
import com.andlife.network.model.invitation.InvitationSaveRequest
import com.andlife.network.model.invitation.InvitationCardRequest
import kotlinx.serialization.json.Json

fun InvitationSaveParam.toRequest(
    json: Json,
): InvitationSaveRequest {
    return InvitationSaveRequest(
        title = title,
        displayHostName = displayHostName,
        thumbnailUrls = thumbnailUrls,
        invitationDate = invitationDate.toString(),
        startTime = startTime.toString(),
        endTime = endTime?.toString(),
        placename = placename,
        address = address,
        latitude = latitude,
        longitude = longitude,
        locationGuide = locationGuide,
        invitationCard = invitationCard?.toRequest(json),
        announcements = announcements.map { it.toRequest() },
    )
}

fun NachoCard.toRequest(json: Json): InvitationCardRequest {
    return InvitationCardRequest(
        contentJson = json.encodeToString(this.toDto()),
        backgroundColor = backgroundColor,
        backgroundImageUrl = backgroundImageUrl,
    )
}

fun AnnouncementSaveParam.toRequest(): AnnouncementRequest {
    return AnnouncementRequest(
        title = title,
        content = content,
        displayOrder = displayOrder,
    )
}
