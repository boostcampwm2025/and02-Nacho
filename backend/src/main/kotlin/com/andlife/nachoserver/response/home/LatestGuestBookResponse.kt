package com.andlife.nachoserver.response.home

import java.time.LocalDateTime

data class LatestGuestBookResponse(
    val id: Long,
    val userName: String,
    val userProfileUrl: String,
    val content: String,
    val createdAt: LocalDateTime,
    val images: List<GuestbookImageResponse> = emptyList(),
    val audios: List<GuestbookAudioResponse> = emptyList(),
    val videos: List<GuestbookVideoResponse> = emptyList()
)

data class GuestbookImageResponse(
    val id: Long,
    val imageUrl: String,
    val displayOrder: Int
)

data class GuestbookAudioResponse(
    val id: Long,
    val audioUrl: String,
    val durationSeconds: Int,
    val displayOrder: Int
)

data class GuestbookVideoResponse(
    val id: Long,
    val videoUrl: String,
    val thumbnailUrl: String,
    val durationSeconds: Int,
    val displayOrder: Int
)