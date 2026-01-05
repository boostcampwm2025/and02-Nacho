package com.andlife.domain.model

import kotlinx.datetime.LocalDateTime

data class GuestBook(
    val id: Long,
    val authorName: String,
    val authorProfileImage: String? = null,
    val invitationTitle: String,
    val textContent: String,
    val visualMedias: List<GuestBookEntryMedia>,
    val audioMedias: List<GuestBookEntryMedia>,
    val totalVisualCount: Int,
    val createdAt: LocalDateTime,
)
