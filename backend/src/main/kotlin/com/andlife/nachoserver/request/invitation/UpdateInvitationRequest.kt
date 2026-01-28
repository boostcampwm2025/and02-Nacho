package com.andlife.nachoserver.request.invitation

data class UpdateInvitationRequest(
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
    val announcements: List<AnnouncementRequest>
)