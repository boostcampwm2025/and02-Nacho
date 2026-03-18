package com.andlife.nachoserver.response.user

import java.time.LocalDateTime

data class FcmTokenResponse(
    val fcmToken: String,
    val userId: Long? = null,
    val createdAt: LocalDateTime
)