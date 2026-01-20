package com.andlife.InvitationServer.response.invitation

data class UpcomingInvitationResponse(
    val id: Long,
    val title: String,
    val thumbnailUrl: String?,
    val invitationDate: String,
    val startTime: String,
    val displayHostName: String,
    val hostProfileUrl: String?,
)
