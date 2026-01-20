package com.andlife.InvitationServer.request.invitation.guestbook

data class GuestBookUpdateRequest(
    val textContent: String,
    val existingImageIds: List<Long> = emptyList(),
    val existingVideoIds: List<Long> = emptyList(),
    val existingAudioIds: List<Long> = emptyList(),
    val newMedias: List<GuestBookMediaRequest> = emptyList()
)