package com.andlife.InvitationServer.response.invitation.guestbook

import com.andlife.InvitationServer.constant.MediaType
import com.andlife.InvitationServer.entity.GuestBook
import com.andlife.InvitationServer.response.AuthorResponse
import java.time.LocalDateTime

data class GuestBookResponse(
    val id: Long,
    val author: AuthorResponse,
    val invitation: GuestBookInvitationResponse?,
    val textContent: String,
    val visualMedias: List<GuestBookMediaResponse>,
    val audioMedias: List<GuestBookMediaResponse>,
    val totalVisualCount: Int,
    val isOwner: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

data class GuestBookInvitationResponse(
    val id: Long,
    val title: String,
)

data class GuestBookMediaResponse(
    val id: Long,
    val type: MediaType,
    val url: String,
    val thumbnailUrl: String? = null,
    val durationSeconds: Int? = null,
    val displayOrder: Int,
)

fun GuestBook.toGuestBookResponse(): GuestBookResponse {
    val author = AuthorResponse(
        id = user.id,
        name = user.name,
        profileImageUrl = user.profileImageUrl
    )

    val visualMedias = mutableListOf<GuestBookMediaResponse>()
    val audioMedias = mutableListOf<GuestBookMediaResponse>()

    images.forEach { image ->
        visualMedias.add(GuestBookMediaResponse(
            id = image.id,
            type = MediaType.IMAGE,
            url = image.imageUrl,
            displayOrder = image.displayOrder
        ))
    }

    audios.forEach { audio ->
        audioMedias.add(GuestBookMediaResponse(
            id = audio.id,
            type = MediaType.AUDIO,
            url = audio.audioUrl,
            durationSeconds = audio.durationSeconds,
            displayOrder = audio.displayOrder
        ))
    }

    videos.forEach { video ->
        visualMedias.add(GuestBookMediaResponse(
            id = video.id,
            type = MediaType.VIDEO,
            url = video.videoUrl,
            thumbnailUrl = video.thumbnailUrl,
            durationSeconds = video.durationSeconds,
            displayOrder = video.displayOrder
        ))
    }

    return GuestBookResponse(
        id = this.id,
        author = author,
        invitation = GuestBookInvitationResponse(
            id = this.invitation.id,
            title = this.invitation.title
        ),
        textContent = this.textContent,
        visualMedias = visualMedias.sortedBy { it.displayOrder },
        audioMedias = audioMedias.sortedBy { it.displayOrder },
        totalVisualCount = visualMedias.size,
        isOwner = true,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}