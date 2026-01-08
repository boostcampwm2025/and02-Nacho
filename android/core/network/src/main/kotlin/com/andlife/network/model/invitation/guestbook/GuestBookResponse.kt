package com.andlife.network.model.invitation.guestbook

import com.andlife.network.model.AuthorResponse
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@OptIn(InternalSerializationApi::class)
@Serializable
data class GuestBookResponse(
    val id: Long,
    val invitationId: Long,
    val author: AuthorResponse,
    val textContent: String,
    val medias: List<CollectionResponse> = emptyList(),
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)
