package com.andlife.network.model

import kotlinx.serialization.Serializable

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class AuthorResponse(
    val id: Long,
    val name: String,
    val profileImageUrl: String? = null,
)
