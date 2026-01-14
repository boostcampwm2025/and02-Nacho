package com.andlife.ui.model

data class GuestBookEntryMediaUiModel(
    val id: Long,
    val type: UiMediaType,
    val url: String,
    val thumbnailUrl: String? = null,
    val durationSeconds: Int? = null,
    val displayOrder: Int
)
