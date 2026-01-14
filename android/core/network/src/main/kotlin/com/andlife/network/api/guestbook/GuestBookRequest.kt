@file:OptIn(InternalSerializationApi::class)

package com.andlife.network.api.guestbook

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
    val durationSeconds: Int? = null, // 오디오, 비디오인 경우에만 사용
    val thumbnailUrl: String? = null, // 비디오인 경우에만 사용
    val displayOrder: Int,
)
