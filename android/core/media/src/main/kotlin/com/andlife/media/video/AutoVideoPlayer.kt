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
        // 정지 시 마지막 프레임이 남지 않도록 검은 화면 처리하거나 초기화
        playerView.keepScreenOn = false // 화면 켜짐 해제
    }

    fun release() {
        exoPlayer.release()
        playerView.player = null // PlayerView와의 연결 해제
    }

    @OptIn(UnstableApi::class)
    fun setMediaSource(mediaSource: MediaSource) = exoPlayer.setMediaSource(mediaSource)

    fun prepare() = exoPlayer.prepare()
}
