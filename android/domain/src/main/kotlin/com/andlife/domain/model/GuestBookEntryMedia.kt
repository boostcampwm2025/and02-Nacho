package com.andlife.domain.model

data class GuestBookEntryMedia(
    val type: MediaType,
    val url: String,
    val thumbnailUrl: String? = null,
    val durationSeconds: Int? = null,
    val displayOrder: Int
)
