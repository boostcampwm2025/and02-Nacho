package com.andlife.data.repository.invitation.mapper

import com.andlife.database.entity.InvitationSummaryEntity
import com.andlife.domain.model.invitation.InvitationSummary
import com.andlife.network.model.invitation.InvitationSummaryResponse

fun InvitationSummaryResponse.toEntity(
    status: String,
    isMyInvitation: Boolean,
): InvitationSummaryEntity {
    return InvitationSummaryEntity(
        id = id,
        title = title,
        displayHostName = displayHostName,
        thumbnailUrls = thumbnailUrls,
        invitationDate = invitationDate,
        startTime = startTime,
        address = address,
        isOwner = isOwner,
        status = status,
        isMyInvitation = isMyInvitation,
    )
}

fun InvitationSummaryEntity.toDomain(): InvitationSummary {
    return InvitationSummary(
        id = id,
        title = title,
        displayHostName = displayHostName,
        thumbnailUrls = thumbnailUrls,
        invitationDate = invitationDate,
        startTime = startTime,
        address = address,
        isOwner = isOwner
    )
}
