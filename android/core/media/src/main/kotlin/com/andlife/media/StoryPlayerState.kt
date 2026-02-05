package com.andlife.media

interface StoryPlayerState {
    fun play(player: StoryMediaPlayer)
    fun pause(player: StoryMediaPlayer)
    fun stop(player: StoryMediaPlayer)
}

class StoryPlayingState : StoryPlayerState {
    override fun play(player: StoryMediaPlayer) { /* 이미 재생 상태 */ }

    override fun pause(player: StoryMediaPlayer) {
        player.exoPlayer.pause()
        player.setState(StoryPausedState())
    }

    override fun stop(player: StoryMediaPlayer) {
        player.exoPlayer.stop()
        player.setState(StoryStoppedState())
    }
}

class StoryPausedState : StoryPlayerState {
    override fun play(player: StoryMediaPlayer) {
        player.exoPlayer.play()
        player.setState(StoryPlayingState())
    }

    override fun pause(player: StoryMediaPlayer) { /* 이미 일시정지 상태 */ }

    override fun stop(player: StoryMediaPlayer) {
        player.exoPlayer.stop()
        player.setState(StoryStoppedState())
    }
}

class StoryStoppedState : StoryPlayerState {
    override fun play(player: StoryMediaPlayer) {
        player.exoPlayer.prepare()
        player.exoPlayer.play()
        player.setState(StoryPlayingState())
    }

    override fun pause(player: StoryMediaPlayer) { /* 정지 상태에선 일시정지 불가 */ }

    override fun stop(player: StoryMediaPlayer) { /* 이미 정지 상태 */ }
}
