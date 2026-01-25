@file:OptIn(InternalSerializationApi::class)

package com.andlife.network.model.guestbook

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
data class GuestBookRequest(
    val userId: Long,
    val textContent: String,
    val medias: List<GuestBookMediaRequest> = emptyList(),
)

@Serializable
data class GuestBookMediaRequest(
    val mediaType: String,
    val mediaUrl: String,
    val durationSeconds: Int? = null,
    val thumbnailUrl: String? = null,
    val displayOrder: Int,
)
