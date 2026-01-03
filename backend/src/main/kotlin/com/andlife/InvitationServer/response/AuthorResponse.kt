package com.andlife.InvitationServer.response

data class AuthorResponse(
    val userId: Long,
    val name: String,
    val profileImageUrl: String? = null
)