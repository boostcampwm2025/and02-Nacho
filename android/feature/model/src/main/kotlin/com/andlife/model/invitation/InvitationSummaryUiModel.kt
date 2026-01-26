package com.andlife.model.invitation

import com.andlife.domain.model.invitation.InvitationSummary
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime

data class InvitationSummaryUiModel(
    val id: Long,
    val title: String,
    val displayHostName: String,
    val thumbnailUrls: ImmutableList<String>,
    val address: String,
    val invitationDateTime: String,
    val dDayCount: Int?
)

fun InvitationSummary.toUiModel(
    formatDateTime: (LocalDate, LocalTime) -> String
): InvitationSummaryUiModel {
    val eventDate = LocalDate.parse(this.invitationDate)
    val eventTime = LocalTime.parse(this.startTime)
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val daysDiff = today.daysUntil(eventDate)

    return InvitationSummaryUiModel(
        id = id,
        title = title,
        displayHostName = displayHostName,
        thumbnailUrls = thumbnailUrls.toPersistentList(),
        address = address,
        invitationDateTime = formatDateTime(eventDate, eventTime),
        dDayCount = if (daysDiff >= 0) daysDiff else null
    )
}
