package com.andlife.invitation.model.guestbook

import com.andlife.domain.model.GuestBookMedia
import com.andlife.invitation.util.toUiType
import com.andlife.ui.model.UiMediaType
import kotlinx.datetime.LocalDateTime

data class InvitationCollectionUiModel(
    val id: Long,
    val type: UiMediaType,
    val url: String,
    val content: String,
    val authorName: String,
    val authorProfileUrl: String?,
    val createdAt: LocalDateTime,
    val durationSeconds: Int?
)

fun GuestBookMedia.toUiModel(): InvitationCollectionUiModel {
    return InvitationCollectionUiModel(
        id = id,
        type = type.toUiType(),
        url = url,
        content = content,
        authorName = authorName,
        authorProfileUrl = authorProfileUrl,
        createdAt = createdAt,
        durationSeconds = durationSeconds
    )
}
