package com.andlife.myinvitation.model

import com.andlife.domain.model.GuestBookMedia
import com.andlife.myinvitation.util.toUiType
import com.andlife.ui.model.MediaType
import kotlinx.datetime.LocalDateTime

data class MyInvitationMediaUiModel(
    val id: Long,
    val type: MediaType,
    val url: String,
    val content: String,
    val authorName: String,
    val authorProfileUrl: String?,
    val createdAt: LocalDateTime,
    val durationSeconds: Int?
)

fun GuestBookMedia.toUiModel(): MyInvitationMediaUiModel {
    return MyInvitationMediaUiModel(
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
