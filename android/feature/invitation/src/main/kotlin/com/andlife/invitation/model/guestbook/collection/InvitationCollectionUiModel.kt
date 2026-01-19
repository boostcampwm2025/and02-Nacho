package com.andlife.invitation.model.guestbook.collection

import com.andlife.domain.model.guestbook.GalleryMedia
import com.andlife.invitation.util.toUiType
import com.andlife.ui.model.UiMediaType
import kotlinx.datetime.LocalDateTime

data class InvitationCollectionUiModel(
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

fun GalleryMedia.toUiModel(): InvitationCollectionUiModel =
    InvitationCollectionUiModel(
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
