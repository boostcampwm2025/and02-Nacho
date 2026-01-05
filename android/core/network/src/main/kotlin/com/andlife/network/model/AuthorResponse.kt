package com.andlife.network.model

import kotlinx.serialization.Serializable

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class AuthorResponse(
    val userId: Long,
    val name: String,
    val profileImageUrl: String? = null,
)
