package com.andlife.media.video

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.ui.PlayerView

class AutoVideoPlayer(
    val exoPlayer: ExoPlayer,
    val playerView: PlayerView,
    var url: String,
) {
    private var currentState: AutoPlayerState = AutoStoppedState()

    fun setState(state: AutoPlayerState) {
        currentState = state
    }

    fun play() = currentState.play(this)

    fun pause() = currentState.pause(this)

    fun stop() {
        currentState.stop(this)
        playerView.keepScreenOn = false
    }

    fun release() {
        exoPlayer.release()
        playerView.player = null
    }

    fun setMuted(isMuted: Boolean) {
        exoPlayer.volume = if (isMuted) 0f else 1f
    }

    fun seekTo(positionMs: Long) = exoPlayer.seekTo(positionMs)

    @OptIn(UnstableApi::class)
    fun setMediaSource(mediaSource: MediaSource) = exoPlayer.setMediaSource(mediaSource)

    fun prepare() = exoPlayer.prepare()

    fun toggleMute(isMuted: Boolean) = setMuted(isMuted)
}
