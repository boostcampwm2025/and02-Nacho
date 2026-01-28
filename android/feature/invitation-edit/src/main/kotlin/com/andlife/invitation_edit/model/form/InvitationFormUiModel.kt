package com.andlife.invitation_edit.model.form

import android.text.Editable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import java.util.UUID

data class InvitationFormUiModel(
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
    val card: CardUiModel? = null,
) {
    val isValid: Boolean
        get() = title.isNotBlank() &&
            author.isNotBlank() &&
            date != null &&
            startTime != null &&
            placeName.isNotBlank() &&
            placeAddress.isNotBlank() &&
            isEndTimeValid
    val isEndTimeValid: Boolean
        get() {
            if (endTime == null || startTime == null) return true
            val start = LocalTime(startTime.hour, startTime.min)
            val end = LocalTime(endTime.hour, endTime.min)
            return end > start
        }
}

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

data class CardUiModel(
    val editable: Editable,
    val backgroundColor: Int,
    val backgroundImageUrl: String? = null,
)

fun InvitationTimeUiModel.toLocalTime(): LocalTime {
    return LocalTime(hour, min)
}
