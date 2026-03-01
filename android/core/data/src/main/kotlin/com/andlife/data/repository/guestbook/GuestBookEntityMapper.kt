package com.andlife.data.repository.guestbook

import com.andlife.database.entity.GuestBookEntity
import com.andlife.database.entity.MediaCache
import com.andlife.domain.model.guestbook.Author
import com.andlife.domain.model.guestbook.GuestBook
import com.andlife.domain.model.guestbook.GuestBookInvitation
import com.andlife.domain.model.guestbook.GuestBookMedia
import com.andlife.domain.model.guestbook.MediaType
import com.andlife.network.model.guestbook.GuestBookMediaResponse
import com.andlife.network.model.guestbook.GuestBookResponse
import kotlinx.datetime.LocalDateTime

fun GuestBookResponse.toEntity(): GuestBookEntity {
    return GuestBookEntity(
        id = id,
        invitationId = invitation.id,
        authorId = author.id,
        authorName = author.name,
        authorProfileUrl = author.profileImageUrl,
        invitationTitle = invitation.title,
        textContent = textContent,
        totalVisualCount = totalVisualCount,
        isOwner = isOwner,
        isInvitationOwner = isInvitationOwner,
        createdAt = createdAt.toString(),
        updatedAt = updatedAt.toString(),
        visualMedias = visualMedias.map { it.toCache() },
        audioMedias = audioMedias.map { it.toCache() },
    )
}

fun GuestBookMediaResponse.toCache(): MediaCache {
    return MediaCache(
        id = id,
        type = type,
        url = url,
        thumbnailUrl = thumbnailUrl,
        durationSeconds = durationSeconds,
        displayOrder = displayOrder,
    )
}

fun GuestBookEntity.toDomain(): GuestBook {
    return GuestBook(
        id = id,
        author = Author(
            id = authorId,
            name = authorName,
            profileImageUrl = authorProfileUrl
        ),
        invitation = GuestBookInvitation(
            id = invitationId,
            title = invitationTitle
        ),
        textContent = textContent,
        visualMedias = visualMedias.map { it.toDomain() },
        audioMedias = audioMedias.map { it.toDomain() },
        totalVisualCount = totalVisualCount,
        isOwner = isOwner,
        isInvitationOwner = isInvitationOwner,
        createdAt = LocalDateTime.parse(createdAt),
        updatedAt = LocalDateTime.parse(updatedAt),
    )
}

fun MediaCache.toDomain(): GuestBookMedia {
    return GuestBookMedia(
        id = id,
        type = MediaType.fromString(type),
        url = url,
        thumbnailUrl = thumbnailUrl,
        durationSeconds = durationSeconds,
        displayOrder = displayOrder,
    )
}
