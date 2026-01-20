package com.andlife.network.api.guestbook

data class UpdateGuestBookRequest(
    val textContent: String,
    val existingImageIds: List<Long>,
    val existingVideoIds: List<Long>,
    val existingAudioIds: List<Long>,
    val newMedias: List<GuestBookMediaRequest>,
)
