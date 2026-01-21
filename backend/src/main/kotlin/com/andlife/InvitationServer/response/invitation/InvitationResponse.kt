package com.andlife.InvitationServer.response.invitation

data class InvitationResponse(
    val id: Long,
    val hostId: Long,
    val title: String,
    val displayHostName: String,
    val hostProfileUrl: String?,
    val thumbnailUrls: List<String>,
    val invitationDate: String,
    val startTime: String,
    val endTime: String?,
    val placename: String,
    val address: String,
    val lat: Double,
    val lng: Double,
    val locationGuide: String?,
    val invitationCard: InvitationCardResponse?,
    val announcements: List<AnnouncementResponse>,
)

data class InvitationCardResponse(
    val id: Long,
    val invitationId: Long,
    val contentJson: String,
    val backgroundColor: Long,
    val backgroundImageUrl: String?,
)

data class AnnouncementResponse(
    val id: Long,
    val invitationId: Long,
    val title: String,
    val content: String,
    val displayOrder: Int,
)
