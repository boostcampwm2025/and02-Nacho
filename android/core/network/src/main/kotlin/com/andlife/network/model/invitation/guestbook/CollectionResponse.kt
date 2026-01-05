package com.andlife.network.model.invitation.guestbook

import com.andlife.network.model.AuthorResponse
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class CollectionResponse(
    val id: Long,
    val mediaType: String,
    val mediaUrl: String,
    val author: AuthorResponse,
    val content: String,
    val createdAt: LocalDateTime,
    val durationSeconds: Int? = null
)
