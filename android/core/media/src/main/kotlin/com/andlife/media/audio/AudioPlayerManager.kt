package com.andlife.media.audio

import kotlinx.coroutines.flow.StateFlow

interface AudioPlayerManager {
    val currentAudio: StateFlow<AudioPlaybackState?>
    fun togglePlay(url: String)
    fun pause()
    fun stopAll()
    fun release()
}
