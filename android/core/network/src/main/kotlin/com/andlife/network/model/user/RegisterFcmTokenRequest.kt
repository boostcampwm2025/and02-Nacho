package com.andlife.network.model.user

import kotlinx.serialization.Serializable

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class RegisterFcmTokenRequest(
    val fcmToken: String,
)
