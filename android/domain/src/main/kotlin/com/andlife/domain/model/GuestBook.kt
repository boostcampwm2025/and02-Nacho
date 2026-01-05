package com.andlife.domain.model

import kotlinx.datetime.LocalDateTime

data class GuestBook(
    val id: Long,
    val author: GuestBookAuthor,
    val invitationTitle: String,
    val textContent: String,
    val visualMedias: List<GuestBookEntryMedia>,
    val audioMedias: List<GuestBookEntryMedia>,
    val totalVisualCount: Int,
    val createdAt: LocalDateTime,
)
