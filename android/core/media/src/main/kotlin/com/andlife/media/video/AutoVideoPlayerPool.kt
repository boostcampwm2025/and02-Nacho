package com.andlife.media.video

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface AutoVideoPlayerPool {

    val isMuted: StateFlow<Boolean>

    fun preparePlayers(neededCount: Int)

    fun getPlayer(url: String): AutoVideoPlayer

    fun playPlayer(
        url: String,
        itemId: Long,
    )

    fun pausePlayer(url: String)

    fun pauseAllPlayers()
    fun toggleMute()

    fun resumeLastPlayed()

    fun clearCacheById(itemId: Long?)

    fun precacheVideos(urls: List<String>)

    fun resetPool()

    fun releaseAllPlayers()
}

class FakeAutoVideoPlayerPool : AutoVideoPlayerPool {
    override val isMuted: StateFlow<Boolean> = MutableStateFlow(false)
    override fun preparePlayers(neededCount: Int) {}
    override fun getPlayer(url: String): AutoVideoPlayer {
        throw NotImplementedError()
    }

    override fun playPlayer(url: String, itemId: Long) {}
    override fun pausePlayer(url: String) {}
    override fun pauseAllPlayers() {}
    override fun toggleMute() {}
    override fun resumeLastPlayed() {}
    override fun clearCacheById(itemId: Long?) {}
    override fun precacheVideos(urls: List<String>) {}
    override fun resetPool() {}
    override fun releaseAllPlayers() {}
}
