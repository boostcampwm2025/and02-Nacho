package com.andlife.model.guestbook

import com.andlife.domain.model.guestbook.GuestBook
import com.andlife.domain.model.guestbook.GuestBookInvitation
import com.andlife.domain.model.guestbook.GuestBookMedia
import com.andlife.model.common.AuthorUiModel
import com.andlife.model.common.toUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.LocalDateTime

data class GuestBookUiModel(
    val id: Long,
    val invitation: GuestBookInvitationUiModel?,
    val author: AuthorUiModel,
    val textContent: String,
    val visualMedias: ImmutableList<GuestBookMediaUiModel>,
    val audioMedias: ImmutableList<GuestBookMediaUiModel>,
    val totalVisualCount: Int,
    val isOwner: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

fun GuestBook.toUiModel(): GuestBookUiModel = GuestBookUiModel(
    id = id,
    invitation = invitation.toUiModel(),
    author = author.toUiModel(),
    textContent = textContent,
    visualMedias = visualMedias.map { it.toUiModel() }.toImmutableList(),
    audioMedias = audioMedias.map { it.toUiModel() }.toImmutableList(),
    totalVisualCount = totalVisualCount,
    isOwner = isOwner,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

data class GuestBookInvitationUiModel(
    val id: Long,
    val title: String,
)

fun GuestBookInvitation.toUiModel(): GuestBookInvitationUiModel = GuestBookInvitationUiModel(
    id = id,
    title = title,
)

data class GuestBookMediaUiModel(
    val id: Long,
    val type: UiMediaType,
    val url: String,
    val thumbnailUrl: String? = null,
    val durationSeconds: Int? = null,
    val displayOrder: Int
)

fun GuestBookMedia.toUiModel(): GuestBookMediaUiModel = GuestBookMediaUiModel(
    id = id,
    type = UiMediaType.safeValueOf(type.name),
    url = url,
    thumbnailUrl = thumbnailUrl,
    durationSeconds = durationSeconds,
    displayOrder = displayOrder,
)
