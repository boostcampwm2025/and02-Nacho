package com.andlife.domain.model.invitation

import com.andlife.domain.model.card.NachoCard
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

data class Invitation(
    val id: Long,
    val hostId: Long,
    val title: String,
    val displayHostName: String,
    val hostProfileUrl: String?,
    val thumbnailUrls: List<String>,
    val invitationDate: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime?,
    val placename: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val locationGuide: String?,
    val invitationCard: InvitationCard?,
    val thanksCard: NachoCard?,
    val announcements: List<Announcement>,
)

data class InvitationCard(
    val id: Long,
    val invitationId: Long,
    val card: NachoCard,
)

data class Announcement(
    val id: Long,
    val invitationId: Long,
    val title: String,
    val content: String,
    val displayOrder: Int,
)
