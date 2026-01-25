package com.andlife.nachoserver.request.guestbook

data class GuestBookRequest(
    val userId: Long,
    val textContent: String,
    val medias: List<GuestBookMediaRequest> = emptyList()
)

data class GuestBookMediaRequest(
    val mediaType: String,
    val mediaUrl: String,
    val durationSeconds: Int? = null,
    val thumbnailUrl: String? = null,
    val displayOrder: Int,
)