package com.andlife.nachoserver.response.guestbook

import com.andlife.nachoserver.constant.MediaType
import com.andlife.nachoserver.response.AuthorResponse
import java.time.LocalDateTime

data class CollectionResponse(
    val id: Long,
    val mediaType: MediaType,
    val mediaUrl: String,
    val thumbnailUrl: String? = null,
    val author: AuthorResponse,
    val content: String,
    val createdAt: LocalDateTime,
    val durationSeconds: Int? = null
)