package com.andlife.InvitationServer.response.invitation.guestbook

import com.andlife.InvitationServer.constant.MediaType
import com.andlife.InvitationServer.response.AuthorResponse
import java.time.LocalDateTime

data class GuestBookResponse(
    val id: Long,
    val author: AuthorResponse,
    val invitation: GuestBookInvitationResponse,
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