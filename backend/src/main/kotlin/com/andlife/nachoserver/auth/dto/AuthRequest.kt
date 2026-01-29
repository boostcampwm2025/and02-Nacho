package com.andlife.nachoserver.auth.dto

data class KakaoLoginRequest(
    val accessToken: String
)

data class RefreshTokenRequest(
    val refreshToken: String
)
