package com.andlife.domain.model.guestbook

data class GuestBookInvitation(
    val id: Long,
    val hostId: Long,
    val title: String,
)
