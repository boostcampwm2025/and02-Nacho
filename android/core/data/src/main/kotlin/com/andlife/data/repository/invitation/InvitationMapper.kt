package com.andlife.data.repository.invitation

import com.andlife.domain.model.invitation.Announcement
import com.andlife.domain.model.invitation.InvitationCard
import com.andlife.domain.model.invitation.Invitation
import com.andlife.domain.model.invitation.InvitationSummary
import com.andlife.network.api.invitation.AnnouncementResponse
import com.andlife.network.api.invitation.InvitationCardResponse
import com.andlife.network.api.invitation.InvitationResponse
import com.andlife.network.model.invitation.InvitationSummaryResponse
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

fun InvitationResponse.toDomain(): Invitation {
    return Invitation(
        id = id,
        hostId = hostId,
        title = title,
        displayHostName = displayHostName,
        hostProfileUrl = hostProfileUrl,
        thumbnailUrls = thumbnailUrls,
        invitationDate = LocalDate.parse(invitationDate),
        startTime = parseTime(startTime),
        endTime = endTime?.let { parseTime(it) },
        placename = placename,
        address = address,
        latitude = lat,
        longitude = lng,
        locationGuide = locationGuide,
        invitationCard = invitationCard?.toDomain(),
        announcements = announcements.map { it.toDomain() },
    )
}

fun InvitationCardResponse.toDomain(): InvitationCard {
    return InvitationCard(
        id = id,
        invitationId = invitationId,
        contentJson = contentJson,
        backgroundImageUrl = backgroundImageUrl,
    )
}

fun AnnouncementResponse.toDomain(): Announcement {
    return Announcement(
        id = id,
        invitationId = invitationId,
        title = title,
        content = content,
        displayOrder = displayOrder,
    )
}

fun InvitationSummaryResponse.toDomain(): InvitationSummary {
    return InvitationSummary(
        id = id,
        title = title,
        displayHostName = displayHostName,
        thumbnailUrls = thumbnailUrls,
        invitationDate = invitationDate,
        startTime = startTime,
        address = address
    )
}

private fun parseTime(timeString: String): LocalTime {
    return LocalTime.parse(timeString)
}
