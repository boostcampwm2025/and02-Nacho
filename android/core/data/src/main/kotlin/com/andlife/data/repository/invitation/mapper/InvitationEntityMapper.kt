package com.andlife.data.repository.invitation.mapper

import com.andlife.database.entity.InvitationSummaryEntity
import com.andlife.domain.model.invitation.InvitationSummary
import com.andlife.network.model.invitation.InvitationSummaryResponse

fun InvitationSummaryResponse.toEntity(
    status: String,
    isMyinvitation: Boolean,
    sortType: String,
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
        isMyinvitation = isMyinvitation,
        sortType = sortType
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
