package com.andlife.nachoserver.auth.dto

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserResponse
)

data class UserResponse(
    val id: Long,
    val email: String,
    val name: String,
    val profileImageUrl: String?
)
