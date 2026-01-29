package com.andlife.network.model.auth

import kotlinx.serialization.Serializable

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class UserResponse(
    val id: Long,
    val email: String,
    val name: String,
    val profileImageUrl: String?
)
