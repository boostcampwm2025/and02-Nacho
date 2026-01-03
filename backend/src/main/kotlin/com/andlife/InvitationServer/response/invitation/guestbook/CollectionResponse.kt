package com.andlife.InvitationServer.response.invitation.guestbook

import com.andlife.InvitationServer.constant.MediaType
import com.andlife.InvitationServer.response.AuthorResponse
import java.time.LocalDateTime

data class CollectionResponse(
    val id: Long,
    val mediaType: MediaType,
    val mediaUrl: String,
    val author: AuthorResponse,
    val createdAt: LocalDateTime,
    val durationSeconds: Int? = null
)