package com.andlife.domain.model

import kotlinx.datetime.LocalDateTime

data class GuestBook(
    val id: Long,
    val author: GuestBookAuthor,
    val invitation: GuestBookInvitation,
    val textContent: String,
    val visualMedias: List<GuestBookMedia>,
    val audioMedias: List<GuestBookMedia>,
    val totalVisualCount: Int,
    val createdAt: LocalDateTime,
)
