package com.andlife.data.repository.invitation.mapper

import com.andlife.domain.model.card.NachoCard
import com.andlife.domain.model.invitation.CreateAnnouncementParam
import com.andlife.domain.model.invitation.CreateInvitationParam
import com.andlife.network.api.invitation.AnnouncementRequest
import com.andlife.network.api.invitation.CreateInvitationRequest
import com.andlife.network.api.invitation.InvitationCardRequest
import kotlinx.serialization.json.Json

fun CreateInvitationParam.toRequest(
    json: Json,
): CreateInvitationRequest {
    return CreateInvitationRequest(
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

fun CreateAnnouncementParam.toRequest(): AnnouncementRequest {
    return AnnouncementRequest(
        title = title,
        content = content,
        displayOrder = displayOrder,
    )
}
