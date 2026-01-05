package com.andlife.domain.model

data class GuestBookAuthor(
    val id: Long,
    val name: String,
    val profileImageUrl: String? = null,
)
