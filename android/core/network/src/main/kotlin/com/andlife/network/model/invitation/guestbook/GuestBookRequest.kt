package com.andlife.network.model.invitation.guestbook

import kotlinx.serialization.Serializable

@Serializable
data class GuestBookRequest(
    val invitationId: Long,
    val userId: Long,
    val textContent: String,
    val medias: List<MediaRequest> = emptyList(),
)
