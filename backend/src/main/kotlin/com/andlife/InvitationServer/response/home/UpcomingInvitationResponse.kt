package com.andlife.InvitationServer.response.home

import java.time.LocalDateTime

data class UpcomingInvitationResponse(
    val id: Long,
    val title: String,
    val hostName: String,
    val startTime: LocalDateTime,
    val thumbnailUrl: String,
    val dDay: Int
)