package com.andlife.nachoserver.response.user

import java.time.LocalDateTime

data class RegisterFcmTokenResponse(
    val fcmToken: String,
    val userId: Long? = null,
    val createdAt: LocalDateTime
)