package com.andlife.InvitationServer.request.invitation.guestbook

data class CreateGuestBookRequest(
    val invitationId: Long,
    val userId: Long,
    val textContent: String,
    val medias: List<MediaRequest> = emptyList()
)
