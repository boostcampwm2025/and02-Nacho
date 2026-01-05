package com.andlife.data.datasource.remote.invitation.guestbook

import com.andlife.domain.model.GuestBook
import com.andlife.domain.model.GuestBookEntryMedia
import com.andlife.domain.model.MediaType
import com.andlife.network.api.guestbook.GuestBookEntryMediaResponse
import com.andlife.network.api.guestbook.GuestBookResponse

fun GuestBookResponse.toDomain(): GuestBook {
    return GuestBook(
        id = id,
        authorName = authorName,
        authorProfileImage = authorProfileImage,
        invitationTitle = invitationTitle,
        textContent = textContent,
        visualMedias = visualMedias.map { it.toDomain() },
        audioMedias = audioMedias.map { it.toDomain() },
        totalVisualCount = totalVisualCount,
        createdAt = createdAt,
    )
}

fun GuestBookEntryMediaResponse.toDomain(): GuestBookEntryMedia {
    return GuestBookEntryMedia(
        type = MediaType.fromString(type),
        url = url,
        thumbnailUrl = thumbnailUrl,
        durationSeconds = durationSeconds,
        displayOrder = displayOrder
    )
}
