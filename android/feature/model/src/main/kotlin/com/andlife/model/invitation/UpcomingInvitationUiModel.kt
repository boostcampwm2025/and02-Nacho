package com.andlife.model.invitation

data class UpcomingInvitationUiModel(
    val id: Long,
    val thumbnailUrl: String,
    val title: String,
    val startTime: DateTimeInfo = DateTimeInfo(),
    val hostInfo: HostInfo = HostInfo(),
)
