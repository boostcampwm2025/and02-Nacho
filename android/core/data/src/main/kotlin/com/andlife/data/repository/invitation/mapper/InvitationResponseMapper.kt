package com.andlife.data.repository.invitation.mapper

import com.andlife.domain.model.invitation.Announcement
import com.andlife.domain.model.invitation.Invitation
import com.andlife.domain.model.invitation.InvitationSummary
import com.andlife.domain.model.invitation.InvitationCard
import com.andlife.domain.model.invitation.UpcomingInvitation
import com.andlife.network.api.invitation.AnnouncementResponse
import com.andlife.network.api.invitation.InvitationCardResponse
import com.andlife.network.api.invitation.InvitationResponse
import com.andlife.network.model.invitation.InvitationSummaryResponse
import com.andlife.network.model.card.NachoCardDto
import com.andlife.network.api.invitation.UpcomingInvitationResponse
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.json.Json

fun InvitationResponse.toDomain(json: Json): Invitation {
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
        invitationCard = invitationCard?.toDomain(json),
        announcements = announcements.map { it.toDomain() },
    )
}

fun InvitationCardResponse.toDomain(json: Json): InvitationCard {
    val nachoCardDto = json.decodeFromString<NachoCardDto>(contentJson)
    val nachoCard = nachoCardDto.toDomain(id = id)

    return InvitationCard(
        id = id,
        invitationId = invitationId,
        card = nachoCard,
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

fun UpcomingInvitationResponse.toDomain(): UpcomingInvitation {
    return UpcomingInvitation(
        id = id,
        hostId = hostId,
        isOwner = isOwner,
        title = title,
        thumbnailUrl = thumbnailUrl,
        invitationDate = LocalDate.parse(invitationDate),
        startTime = LocalTime.parse(startTime),
        displayHostName = displayHostName,
    )
}
