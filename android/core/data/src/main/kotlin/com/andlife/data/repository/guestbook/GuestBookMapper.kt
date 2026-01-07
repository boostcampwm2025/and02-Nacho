package com.andlife.data.repository.guestbook

import com.andlife.domain.model.GuestBook
import com.andlife.domain.model.GuestBookAuthor
import com.andlife.domain.model.GuestBookEntryMedia
import com.andlife.domain.model.GuestBookInvitation
import com.andlife.domain.model.GuestBookMedia
import com.andlife.domain.model.MediaType
import com.andlife.network.api.guestbook.GuestBookAuthorResponse
import com.andlife.network.api.guestbook.GuestBookEntryMediaResponse
import com.andlife.network.api.guestbook.GuestBookInvitationResponse
import com.andlife.network.api.guestbook.GuestBookResponse
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

fun GuestBookResponse.toDomain(): GuestBook =
    GuestBook(
        id = id,
        author = author.toDomain(),
        invitation = invitation.toDomain(),
        textContent = textContent,
        visualMedias = visualMedias.map { it.toDomain() },
        audioMedias = audioMedias.map { it.toDomain() },
        totalVisualCount = totalVisualCount,
        createdAt = createdAt,
    )

fun GuestBookAuthorResponse.toDomain(): GuestBookAuthor =
    GuestBookAuthor(
        id = id,
        name = name,
        profileImageUrl = profileImageUrl,
    )

fun GuestBookInvitationResponse.toDomain(): GuestBookInvitation =
    GuestBookInvitation(
        id = id,
        title = title,
    )

fun GuestBookEntryMediaResponse.toDomain(): GuestBookEntryMedia =
    GuestBookEntryMedia(
        id = id,
        type = MediaType.fromString(type),
        url = url,
        thumbnailUrl = thumbnailUrl,
        durationSeconds = durationSeconds,
        displayOrder = displayOrder,
    )
