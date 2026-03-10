package com.andlife.domain.model.guestbook

data class GuestBookMedia(
    val id: Long?, // null이면 새로 추가된 미디어
    val type: MediaType,
    val url: String,
    val thumbnailUrl: String? = null,
    val durationSeconds: Int? = null,
    val displayOrder: Int,
)
