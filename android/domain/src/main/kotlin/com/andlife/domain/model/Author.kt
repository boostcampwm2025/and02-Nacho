package com.andlife.domain.model

data class Author(
    val id: Long,
    val name: String,
    val profileImageUrl: String? = null,
)
