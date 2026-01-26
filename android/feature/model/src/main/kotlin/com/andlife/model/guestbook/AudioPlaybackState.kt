package com.andlife.model.guestbook

data class AudioPlaybackState(
    val playingAudioUrl: String? = null,
    val isAudioPlaying: Boolean = false,
    val audioCurrentPositionMs: Long = 0L,
    val audioTotalDurationMs: Long = 0L,
) {
    fun isAudioPlayingForGuestBook(guestBookAudioUrls: List<String>): Boolean =
        isAudioPlaying && guestBookAudioUrls.any { it == playingAudioUrl }

    fun isAudioPlayingForUrl(url: String): Boolean =
        isAudioPlaying && playingAudioUrl == url
}
