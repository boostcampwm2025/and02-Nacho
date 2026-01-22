@file:OptIn(InternalSerializationApi::class)

package com.andlife.network.api.guestbook

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
data class UpdateGuestBookRequest(
    val textContent: String,
    val existingImageIds: List<Long>,
    val existingVideoIds: List<Long>,
    val existingAudioIds: List<Long>,
    val newMedias: List<GuestBookMediaRequest>,
)
