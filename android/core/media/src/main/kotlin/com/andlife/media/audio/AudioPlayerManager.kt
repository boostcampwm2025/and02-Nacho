package com.andlife.media.audio

import kotlinx.coroutines.flow.StateFlow

interface AudioPlayerManager {
    val currentTrack: StateFlow<AudioInfo?>
    fun togglePlay(url: String)
    fun pause()
    fun stopAll()
    fun release()
}
