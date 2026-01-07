package com.andlife.domain.model

import kotlinx.datetime.LocalDateTime

data class GuestBookMedia(
    val id: Long,
    val type: MediaType,
    val url: String,
    val content: String,
    val authorName: String,
    val authorProfileUrl: String?,
    val createdAt: LocalDateTime,
    val durationSeconds: Int?
)
