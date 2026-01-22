package com.andlife.model.invitation

import com.andlife.domain.model.invitation.UpcomingInvitation

fun UpcomingInvitation.toUpcomingInvitationUiModel(): UpcomingInvitationUiModel {
    return UpcomingInvitationUiModel(
        id = id,
        hostId = hostId,
        thumbnailUrl = thumbnailUrl ?: "",
        title = title,
        startTime = DateTimeInfo(
            date = invitationDate,
            startTime = TimeUiModel(
                hour = startTime.hour,
                min = startTime.minute,
            ),
        ),
        hostInfo = HostInfo(
            name = displayHostName,
            profileUrl = null,
        ),
    )
}
