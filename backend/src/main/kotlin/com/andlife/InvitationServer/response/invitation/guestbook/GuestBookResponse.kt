package com.andlife.InvitationServer.response.invitation.guestbook

import com.andlife.InvitationServer.constant.MediaType
import com.andlife.InvitationServer.entity.GuestBook
import com.andlife.InvitationServer.response.AuthorResponse
import java.time.LocalDateTime

data class GuestBookResponse(
    val id: Long,
    val invitationId: Long,
    val author: AuthorResponse,
    val textContent: String,
    val medias: List<CollectionResponse> = emptyList(),
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

fun GuestBook.toGuestBookResponse(): GuestBookResponse {
    val author = AuthorResponse(
        id = user.id,
        name = user.name,
        profileImageUrl = user.profileImageUrl
    )

    val medias = mutableListOf<CollectionResponse>()

    // 이미지 추가
    images.forEach { image ->
        medias.add(CollectionResponse(
            id = image.id,
            mediaType = MediaType.IMAGE,
            mediaUrl = image.imageUrl,
            author = author,
            content = textContent,
            createdAt = createdAt,
            durationSeconds = 0
        ))
    }

    // 오디오 추가
    audios.forEach { audio ->
        medias.add(CollectionResponse(
            id = audio.id,
            mediaType = MediaType.AUDIO,
            mediaUrl = audio.audioUrl,
            author = author,
            content = textContent,
            createdAt = createdAt,
            durationSeconds = audio.durationSeconds
        ))
    }

    // 비디오 추가
    videos.forEach { video ->
        medias.add(CollectionResponse(
            id = video.id,
            mediaType = MediaType.VIDEO,
            mediaUrl = video.videoUrl,
            author = author,
            content = textContent,
            createdAt = createdAt,
            durationSeconds = video.durationSeconds
        ))
    }

    return GuestBookResponse(
        id = id,
        invitationId = invitation.id,
        author = author,
        textContent = textContent,
        medias = medias.sortedBy { it.id },
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

