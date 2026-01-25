package com.andlife.model.collection

import com.andlife.domain.model.guestbook.GalleryMedia
import com.andlife.model.guestbook.UiMediaType
import com.andlife.model.util.toUiType
import kotlinx.datetime.LocalDateTime

data class CollectionUiModel(
    val id: Long,
    val type: UiMediaType,
    val mediaUrl: String,
    val thumbnailUrl: String? = null,
    val content: String,
    val authorName: String,
    val authorProfileUrl: String?,
    val createdAt: LocalDateTime,
    val durationSeconds: Int?,
)

fun GalleryMedia.toUiModel(): CollectionUiModel =
    CollectionUiModel(
        id = id,
        type = type.toUiType(),
        mediaUrl = mediaUrl,
        thumbnailUrl = thumbnailUrl,
        content = content,
        authorName = authorName,
        authorProfileUrl = authorProfileUrl,
        createdAt = createdAt,
        durationSeconds = durationSeconds,
    )
