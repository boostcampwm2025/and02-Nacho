package com.andlife.invitation_edit.model.create

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.LocalDate
import java.util.UUID

data class CreateInvitationUiModel(
    val title: String = "",
    val author: String = "",
    val imageList: ImmutableList<ThumbnailImageUiModel> = persistentListOf(),
    val date: LocalDate? = null,
    val startTime: InvitationTimeUiModel? = null,
    val endTime: InvitationTimeUiModel? = null,
    val placeName: String = "",
    val placeAddress: String = "",
    val placeGuide: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val announcement: ImmutableList<AnnouncementUiModel> = persistentListOf(),
)

data class ThumbnailImageUiModel(
    val id: String = UUID.randomUUID().toString(),
    val url: String,
)

data class InvitationTimeUiModel(
    val hour: Int,
    val min: Int,
)

data class AnnouncementUiModel(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
)
