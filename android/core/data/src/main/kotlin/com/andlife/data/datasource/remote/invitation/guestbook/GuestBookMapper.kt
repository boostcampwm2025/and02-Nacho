package com.andlife.data.datasource.remote.invitation.guestbook

import com.andlife.domain.model.GuestBook
import com.andlife.domain.model.GuestBookAuthor
import com.andlife.domain.model.GuestBookEntryMedia
import com.andlife.domain.model.GuestBookInvitation
import com.andlife.domain.model.MediaType
import com.andlife.network.api.guestbook.GuestBookAuthorResponse
import com.andlife.network.api.guestbook.GuestBookEntryMediaResponse
import com.andlife.network.api.guestbook.GuestBookInvitationResponse
import com.andlife.network.api.guestbook.GuestBookResponse

fun GuestBookResponse.toDomain(): GuestBook =
    GuestBook(
        id = id,
        author = author.toDomain(),
        invitation = invitation.toDomain(),
        textContent = textContent,
        visualMedias = visualMedias.map { it.toDomain() },
        audioMedias = audioMedias.map { it.toDomain() },
        totalVisualCount = totalVisualCount,
        isAuthorSelf = isAuthorSelf,
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
