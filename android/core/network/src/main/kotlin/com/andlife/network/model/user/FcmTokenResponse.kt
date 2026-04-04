package com.andlife.network.model.user

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class FcmTokenResponse(
    val id: Long,
    val token: String,
    val updatedAt: LocalDateTime,
    val lastPushedAt: LocalDateTime?,
)
