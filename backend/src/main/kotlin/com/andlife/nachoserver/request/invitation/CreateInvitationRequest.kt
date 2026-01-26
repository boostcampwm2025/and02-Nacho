package com.andlife.nachoserver.request.invitation

data class CreateInvitationRequest(
    val title: String,
    val displayHostName: String,
    val thumbnailUrls: List<String>,
    val invitationDate: String,
    val startTime: String,
    val endTime: String?,
    val placename: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val locationGuide: String?,
    val invitationCard: InvitationCardRequest?,
    val announcements: List<AnnouncementRequest>,
)


data class InvitationCardRequest(
    val contentJson: String,           // NachoCard를 JSON 직렬화한 문자열
    val backgroundColor: Long,
    val backgroundImageUrl: String?,
)

data class AnnouncementRequest(
    val title: String,
    val content: String,
    val displayOrder: Int,
)