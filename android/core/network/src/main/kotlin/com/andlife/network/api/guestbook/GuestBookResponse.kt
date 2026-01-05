@file:OptIn(InternalSerializationApi::class)

package com.andlife.network.api.guestbook

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
data class GuestBookResponse(
    val id: Long,
    val writerName: String,
    val writerProfileImage: String? = null,
    val invitationTitle: String,
    val textContent: String,
    val visualMedias: List<GuestBookMediaResponse>,
    val audioMedias: List<GuestBookMediaResponse>,
    val totalVisualCount: Int,
    val createdAt: LocalDateTime,
)

@Serializable
data class GuestBookMediaResponse(
    val type: String,
    val url: String,
    val thumbnailUrl: String? = null,
    val durationSeconds: Int? = null,
    val displayOrder: Int
)
