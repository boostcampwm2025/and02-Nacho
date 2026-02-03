package com.andlife.media

import androidx.media3.exoplayer.ExoPlayer

class StoryMediaPlayer(
    val exoPlayer: ExoPlayer,
    initialState: StoryPlayerState = StoryStoppedState()
) {
    private var currentState: StoryPlayerState = initialState

    fun setState(state: StoryPlayerState) {
        this.currentState = state
    }

    fun play() = currentState.play(this)
    fun pause() = currentState.pause(this)
    fun stop() = currentState.stop(this)

    fun release() = exoPlayer.release()
}
