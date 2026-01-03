package com.andlife.InvitationServer.response

data class AuthorResponse(
    val userId: Long,
    val nickname: String,
    val profileImageUrl: String? = null
)