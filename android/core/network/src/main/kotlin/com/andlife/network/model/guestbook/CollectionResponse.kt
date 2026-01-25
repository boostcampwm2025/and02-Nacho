package com.andlife.network.model.guestbook

import com.andlife.network.model.AuthorResponse
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@OptIn(InternalSerializationApi::class)
@Serializable
data class CollectionResponse(
    val id: Long,
    val mediaType: String,
    val mediaUrl: String,
    val thumbnailUrl: String? = null,
    val author: AuthorResponse,
    val content: String,
    val createdAt: LocalDateTime,
    val durationSeconds: Int? = null
)
