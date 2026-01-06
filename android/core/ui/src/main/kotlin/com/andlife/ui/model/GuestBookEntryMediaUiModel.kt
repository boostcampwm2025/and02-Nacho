package com.andlife.ui.model

data class GuestBookEntryMediaUiModel(
    val id: Long,
    val type: MediaType,
    val url: String,
    val thumbnailUrl: String? = null,
    val durationSeconds: Int? = null,
    val displayOrder: Int
)
