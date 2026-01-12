package com.andlife.myinvitation.model

import com.andlife.domain.model.guestbook.GalleryMedia
import com.andlife.myinvitation.util.toUiType
import com.andlife.ui.model.UiMediaType
import kotlinx.datetime.LocalDateTime

data class MyInvitationCollectionUiModel(
    val id: Long,
    val type: UiMediaType,
    val url: String,
    val content: String,
    val authorName: String,
    val authorProfileUrl: String?,
    val createdAt: LocalDateTime,
    val durationSeconds: Int?,
)

fun GalleryMedia.toUiModel(): MyInvitationCollectionUiModel =
    MyInvitationCollectionUiModel(
        id = id,
        type = type.toUiType(),
        url = mediaUrl,
        content = content,
        authorName = authorName,
        authorProfileUrl = authorProfileUrl,
        createdAt = createdAt,
        durationSeconds = durationSeconds,
    )
