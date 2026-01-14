package com.andlife.media.video

interface AutoPlayerState {
    fun play(player: AutoVideoPlayer)

    fun pause(player: AutoVideoPlayer)

    fun stop(player: AutoVideoPlayer)
}

class AutoPlayingState : AutoPlayerState {
    override fun play(player: AutoVideoPlayer) { /* 이미 재생 중 */}

    override fun pause(player: AutoVideoPlayer) {
        player.exoPlayer.pause()
        player.setState(AutoPausedState())
    }

    override fun stop(player: AutoVideoPlayer) {
        player.exoPlayer.stop()
        player.setState(AutoStoppedState())
    }
}

class AutoPausedState : AutoPlayerState {
    override fun play(player: AutoVideoPlayer) {
        player.exoPlayer.play()
        player.setState(AutoPlayingState())
    }

    override fun pause(player: AutoVideoPlayer) { /* 이미 일시정지 */ }

    override fun stop(player: AutoVideoPlayer) {
        player.exoPlayer.stop()
        player.setState(AutoStoppedState())
    }
}

class AutoStoppedState : AutoPlayerState {
    override fun play(player: AutoVideoPlayer) {
        player.exoPlayer.prepare()
        player.exoPlayer.play()
        player.setState(AutoPlayingState())
    }

    override fun pause(player: AutoVideoPlayer) { /* 정지 상태에서는 일시정지 불가 */ }

    override fun stop(player: AutoVideoPlayer) { /* 이미 정지 */ }
}
