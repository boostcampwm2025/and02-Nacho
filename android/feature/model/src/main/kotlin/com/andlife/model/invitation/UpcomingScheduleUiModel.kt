package com.andlife.model.invitation

data class UpcomingScheduleUiModel(
    val id: Long,
    val thumbnailUrl: String,
    val title: String,
    val dateTime: DateTimeInfo = DateTimeInfo(),
    val hostInfo: HostInfo = HostInfo(),
)
