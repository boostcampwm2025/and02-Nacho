package com.andlife.data.repository.guestbook

import com.andlife.domain.model.GuestBook
import com.andlife.domain.model.GuestBookMedia
import com.andlife.domain.model.MediaType
import com.andlife.network.model.invitation.guestbook.CollectionResponse
import com.andlife.network.model.invitation.guestbook.GuestBookResponse
import com.andlife.network.model.invitation.guestbook.MediaRequest

fun GuestBookMedia.toRequest(): MediaRequest =
    MediaRequest(
        mediaType = type.toString(),
        mediaUrl = url,
        durationSeconds = durationSeconds,
        thumbnailUrl = null,
        displayOrder = 0,
    )

fun GuestBookResponse.toDomain(): GuestBook =
    GuestBook(
        id = id,
        invitationId = invitationId,
        authorName = author.name,
        authorProfileUrl = author.profileImageUrl,
        textContent = textContent,
        medias = medias.map { it.toDomain() },
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

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
