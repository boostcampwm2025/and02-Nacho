package com.andlife.media

import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class StoryMediaPlayer(
    internal val exoPlayer: ExoPlayer
) {
    private var state: StoryPlayerState = StoryStoppedState()

    val playbackState: Int get() = exoPlayer.playbackState

    fun play() {
        state.play(this)
    }

    fun pause() {
        state.pause(this)
    }

    fun stop() {
        state.stop(this)
    }

    fun prepare() {
        exoPlayer.prepare()
    }

    fun release() {
        exoPlayer.release()
    }

    internal fun setState(newState: StoryPlayerState) {
        state = newState
    }

    fun getExoPlayer(): ExoPlayer = exoPlayer

    fun addListener(listener: Player.Listener) {
        exoPlayer.addListener(listener)
    }

    fun removeListener(listener: Player.Listener) {
        exoPlayer.removeListener(listener)
    }
}
