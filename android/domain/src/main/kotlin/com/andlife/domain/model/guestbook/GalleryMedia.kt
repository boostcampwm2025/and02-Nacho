package com.andlife.domain.model.guestbook

import kotlinx.datetime.LocalDateTime

data class GalleryMedia(
    val id: Long,
    val type: MediaType,
    val mediaUrl: String,
    val thumbnailUrl: String?,
    val content: String,
    val authorName: String,
    val authorProfileUrl: String?,
    val createdAt: LocalDateTime,
    val durationSeconds: Int?
)
