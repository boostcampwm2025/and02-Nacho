package com.andlife.nachoserver.response.invitation

data class UpcomingInvitationResponse(
    val id: Long,
    val hostId: Long,
    val isOwner: Boolean,
    val title: String,
    val thumbnailUrl: String?,
    val invitationDate: String,
    val startTime: String,
    val displayHostName: String,
    val hostProfileUrl: String?,
)
