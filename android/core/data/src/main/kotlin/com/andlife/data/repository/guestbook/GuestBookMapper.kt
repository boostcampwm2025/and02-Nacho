package com.andlife.data.repository.guestbook

import com.andlife.domain.model.guestbook.Author
import com.andlife.domain.model.guestbook.GalleryMedia
import com.andlife.domain.model.guestbook.GuestBook
import com.andlife.domain.model.guestbook.GuestBookInvitation
import com.andlife.domain.model.guestbook.GuestBookMedia
import com.andlife.domain.model.guestbook.MediaType
import com.andlife.network.api.guestbook.GuestBookInvitationResponse
import com.andlife.network.api.guestbook.GuestBookMediaResponse
import com.andlife.network.api.guestbook.GuestBookResponse
import com.andlife.network.model.AuthorResponse
import com.andlife.network.model.invitation.guestbook.CollectionResponse

fun CollectionResponse.toDomain(): GalleryMedia =
    GalleryMedia(
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
        isOwner = isOwner,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

fun AuthorResponse.toDomain(): Author =
    Author(
        id = id,
        name = name,
        profileImageUrl = profileImageUrl,
    )

fun GuestBookInvitationResponse.toDomain(): GuestBookInvitation =
    GuestBookInvitation(
        id = id,
        title = title,
    )

fun GuestBookMediaResponse.toDomain(): GuestBookMedia =
    GuestBookMedia(
        id = id,
        type = MediaType.fromString(type),
        url = url,
        thumbnailUrl = thumbnailUrl,
        durationSeconds = durationSeconds,
        displayOrder = displayOrder,
    )
