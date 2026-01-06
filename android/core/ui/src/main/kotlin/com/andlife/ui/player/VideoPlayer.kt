package com.andlife.ui.player

import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource

class VideoPlayer(
    val exoPlayer: ExoPlayer,
    val uri: String,
) {
    var currentState: PlayerState = StoppedState()

    fun setState(state: PlayerState) {
        currentState = state
    }

    fun play() = currentState.play(this)
    fun pause() = currentState.pause(this)
    fun stop() = currentState.stop(this)

    fun release() {
        exoPlayer.release()
    }

    fun setMediaSource(mediaSource: MediaSource) {
        exoPlayer.setMediaSource(mediaSource)
    }

    fun prepare() {
        exoPlayer.prepare()
    }
}
