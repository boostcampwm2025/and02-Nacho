package com.andlife.network.model.auth

import kotlinx.serialization.Serializable

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class RefreshTokenRequest(
    val refreshToken: String
)
