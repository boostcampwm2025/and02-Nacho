@file:OptIn(InternalSerializationApi::class)

package com.andlife.network.api.guestbook

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
data class GuestBookResponse(
    val id: Long,
    val authorName: String,
    val authorProfileImage: String?,
    val invitationTitle: String,
    val textContent: String,
    val visualMedias: List<GuestBookEntryMediaResponse>,
    val audioMedias: List<GuestBookEntryMediaResponse>,
    val totalVisualCount: Int,
    val createdAt: LocalDateTime,
)

@Serializable
data class GuestBookEntryMediaResponse(
    val type: String,
    val url: String,
    val thumbnailUrl: String?,
    val durationSeconds: Int?,
    val displayOrder: Int,
)
