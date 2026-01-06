package com.andlife.data.repository.guestbook

import com.andlife.domain.model.GuestBookMedia
import com.andlife.domain.model.MediaType
import com.andlife.network.model.invitation.guestbook.CollectionResponse

fun CollectionResponse.toDomain(): GuestBookMedia =
    GuestBookMedia(
        id = id,
        type = MediaType.fromString(mediaType),
        url = mediaUrl,
        content = content,
        authorName = author.name,
        authorProfileUrl = author.profileImageUrl,
        createdAt = createdAt,
        durationSeconds = durationSeconds,
    )
