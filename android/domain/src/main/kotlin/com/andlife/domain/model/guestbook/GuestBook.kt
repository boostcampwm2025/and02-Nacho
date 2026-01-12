package com.andlife.domain.model.guestbook

import kotlinx.datetime.LocalDateTime

data class GuestBook(
    val id: Long,
    val author: Author,
    val invitation: GuestBookInvitation,
    val textContent: String,
    val visualMedias: List<GuestBookMedia>,
    val audioMedias: List<GuestBookMedia>,
    val totalVisualCount: Int,
    val isOwner: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)
