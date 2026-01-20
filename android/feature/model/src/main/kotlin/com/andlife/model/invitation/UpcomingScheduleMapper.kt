package com.andlife.model.invitation

import com.andlife.domain.model.invitation.Invitation

fun Invitation.toUpcomingScheduleUiModel(): UpcomingScheduleUiModel {
    return UpcomingScheduleUiModel(
        id = id,
        thumbnailUrl = thumbnailUrls.firstOrNull() ?: "",
        title = title,
        dateTime = DateTimeInfo(
            date = invitationDate,
            startTime = TimeUiModel(
                hour = startTime.hour,
                min = startTime.minute,
            ),
        ),
        hostInfo = HostInfo(
            name = displayHostName,
            profileUrl = hostProfileUrl,
        ),
    )
}
