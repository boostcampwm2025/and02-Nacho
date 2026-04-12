package com.andlife.nachoserver.response.user

import java.time.LocalDateTime

data class FcmTokenResponse(
    val fcmToken: String,
    val userId: Long? = null,
    val lastPushedAt: LocalDateTime? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)