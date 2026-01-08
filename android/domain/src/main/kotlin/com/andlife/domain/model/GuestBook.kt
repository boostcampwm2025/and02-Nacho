package com.andlife.domain.model

import kotlinx.datetime.LocalDateTime

data class GuestBook(
    val id: Long = 0,
    val invitationId: Long,
    val authorName: String,
    val authorProfileUrl: String? = null,
    val textContent: String,
    val medias: List<GuestBookMedia> = emptyList(),
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)
