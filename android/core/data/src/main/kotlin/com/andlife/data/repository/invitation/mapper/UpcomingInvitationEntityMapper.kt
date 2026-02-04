package com.andlife.data.repository.invitation.mapper

import com.andlife.database.entity.UpcomingInvitationEntity
import com.andlife.domain.model.invitation.UpcomingInvitation
import com.andlife.network.model.invitation.UpcomingInvitationResponse
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

fun UpcomingInvitationResponse.toEntity(): UpcomingInvitationEntity {
    return UpcomingInvitationEntity(
        id = id,
        hostId = hostId,
        isOwner = isOwner,
        title = title,
        thumbnailUrl = thumbnailUrl,
        invitationDate = invitationDate,
        startTime = startTime,
        displayHostName = displayHostName,
    )
}

fun UpcomingInvitationEntity.toDomain(): UpcomingInvitation {
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
