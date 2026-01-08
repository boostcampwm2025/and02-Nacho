package com.andlife.InvitationServer.request.invitation.guestbook

data class MediaRequest(
    val mediaType: String,
    val mediaUrl: String,
    val durationSeconds: Int? = null, // 오디오, 비디오인 경우에만 사용
    val thumbnailUrl: String? = null, // 비디오인 경우에만 사용
    val displayOrder: Int,
)