package com.andlife.domain.model.auth

data class User(
    val id: Long,
    val name: String,
    val email: String,
    val profileImageUrl: String? = null,
)
