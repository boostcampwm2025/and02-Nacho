package com.andlife.domain.model.invitation

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

data class UpcomingInvitation(
    val id: Long,
    val hostId: Long,
    val title: String,
    val thumbnailUrl: String?,
    val invitationDate: LocalDate,
    val startTime: LocalTime,
    val displayHostName: String,
)
