package com.andlife.ui.player

interface PlayerState {
    fun play(player: VideoPlayer)

    fun pause(player: VideoPlayer)

    fun stop(player: VideoPlayer)
}

class PlayingState : PlayerState {
    override fun play(player: VideoPlayer) {
        // 이미 재생 중인 상태이므로 아무 작업도 수행하지 않음
    }

    override fun pause(player: VideoPlayer) {
        player.exoPlayer.pause()
        player.setState(PausedState())
    }

    override fun stop(player: VideoPlayer) {
        player.exoPlayer.stop()
        player.setState(StoppedState())
    }
}

class PausedState : PlayerState {
    override fun play(player: VideoPlayer) {
        player.exoPlayer.play()
        player.setState(PlayingState())
    }

    override fun pause(player: VideoPlayer) {
        // 이미 일시정지 상태이므로 아무 작업도 수행하지 않음
    }

    override fun stop(player: VideoPlayer) {
        player.exoPlayer.stop()
        player.setState(StoppedState())
    }
}

class StoppedState : PlayerState {
    override fun play(player: VideoPlayer) {
        player.exoPlayer.prepare()
        player.exoPlayer.play()
        player.setState(PlayingState())
    }

    override fun pause(player: VideoPlayer) {
        // 정지 상태에서는 일시정지할 수 없음
    }

    override fun stop(player: VideoPlayer) {
        // 이미 정지 상태이므로 아무 작업도 수행하지 않음
    }
}
