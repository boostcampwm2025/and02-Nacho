package com.andlife.media.video

interface AutoVideoPlayerPool {
    fun preparePlayers()
    fun getPlayer(url: String): AutoVideoPlayer
    fun playPlayer(url: String, itemId: Long)
    fun pausePlayer(url: String)
    fun pauseAllPlayers()
    fun resumeLastPlayed()
    fun resetPool()
    fun releaseAllPlayers()
}
