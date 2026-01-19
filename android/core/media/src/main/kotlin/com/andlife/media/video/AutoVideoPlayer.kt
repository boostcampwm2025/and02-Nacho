package com.andlife.media.video

import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource

class AutoVideoPlayer(
    val exoPlayer: ExoPlayer,
    var url: String,
) {
    private var currentState: AutoPlayerState = AutoStoppedState()

    fun setState(state: AutoPlayerState) {
        currentState = state
    }

    fun play() = currentState.play(this)

    fun pause() = currentState.pause(this)

    fun stop() = currentState.stop(this)

    fun release() = exoPlayer.release()

    fun setMediaSource(mediaSource: MediaSource) = exoPlayer.setMediaSource(mediaSource)

    fun prepare() = exoPlayer.prepare()
}
