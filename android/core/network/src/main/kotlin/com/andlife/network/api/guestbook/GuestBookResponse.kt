@file:OptIn(InternalSerializationApi::class)

package com.andlife.network.api.guestbook

import com.andlife.network.model.AuthorResponse
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
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
)

@Serializable
data class GuestBookInvitationResponse(
    val id: Long,
    val title: String,
)

@Serializable
data class GuestBookMediaResponse(
    val id: Long,
    val type: String,
    val url: String,
    val thumbnailUrl: String?,
    val durationSeconds: Int?,
    val displayOrder: Int,
)
