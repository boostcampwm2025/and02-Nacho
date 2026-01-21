package com.andlife.domain.model.invitation

import com.andlife.domain.model.card.NachoCard
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

data class CreateInvitationParam(
    val title: String,
    val displayHostName: String,
    val thumbnailUrls: List<String>,
    val invitationDate: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime?,
    val placename: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val locationGuide: String?,
    val invitationCard: NachoCard?,
    val announcements: List<CreateAnnouncementParam>,
)

data class CreateAnnouncementParam(
    val title: String,
    val content: String,
    val displayOrder: Int,
)
