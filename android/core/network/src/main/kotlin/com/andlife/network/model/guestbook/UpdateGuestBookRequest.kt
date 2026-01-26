package com.andlife.network.model.guestbook

import kotlinx.serialization.Serializable

@Serializable
data class UpdateGuestBookRequest(
    val textContent: String,
    val existingImageIds: List<Long>,
    val existingVideoIds: List<Long>,
    val existingAudioIds: List<Long>,
    val newMedias: List<GuestBookMediaRequest>,
)
