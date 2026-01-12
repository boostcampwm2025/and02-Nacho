package com.andlife.domain.model.guestbook

data class Author(
    val id: Long,
    val name: String,
    val profileImageUrl: String? = null,
)
