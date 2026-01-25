package com.andlife.nachoserver.response

data class AuthorResponse(
    val id: Long,
    val name: String,
    val profileImageUrl: String? = null
)