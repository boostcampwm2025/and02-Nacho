package com.andlife.media.audio

import kotlinx.coroutines.flow.StateFlow

interface AudioPlayerManager {
    val currentAudioUrl: StateFlow<String?>
    val isPlaying: StateFlow<Boolean>

    fun togglePlay(url: String)
    fun pause()
    fun stopAll()
    fun release()
}
