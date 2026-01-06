package com.andlife.InvitationServer.response.invitation.guestbook

import com.andlife.InvitationServer.constant.MediaType
import java.time.LocalDateTime

data class GuestBookResponse(
    val id: Long,
    val author: GuestBookAuthorResponse,
    val invitation: GuestBookInvitationResponse,
    val textContent: String,
    val visualMedias: List<GuestBookEntryMediaResponse>,
    val audioMedias: List<GuestBookEntryMediaResponse>,
    val totalVisualCount: Int,
    val isAuthorSelf: Boolean,
    val createdAt: LocalDateTime,
)

data class GuestBookAuthorResponse(
    val id: Long,
    val name: String,
    val profileImageUrl: String?,
)

data class GuestBookInvitationResponse(
    val id: Long,
    val title: String,
)

data class GuestBookEntryMediaResponse(
    val id: Long,
    val type: MediaType,
    val url: String,
    val thumbnailUrl: String? = null,
    val durationSeconds: Int? = null,
    val displayOrder: Int,
)