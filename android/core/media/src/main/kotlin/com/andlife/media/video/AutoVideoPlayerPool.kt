package com.andlife.media.video

interface AutoVideoPlayerPool {
    fun preparePlayers()

    fun getPlayer(url: String): AutoVideoPlayer

    fun playPlayer(
        url: String,
        itemId: Long,
    )

    fun pausePlayer(url: String)

    fun pauseAllPlayers()

    fun resumeLastPlayed()

    fun clearCacheById(itemId: Long?)

    fun precacheVideos(urls: List<String>)

    fun resetPool()

    fun releaseAllPlayers()
}

class FakeVideoPlayerPool : AutoVideoPlayerPool {
    override fun preparePlayers() {}
    override fun getPlayer(url: String): AutoVideoPlayer {
        throw NotImplementedError()
    }

    override fun playPlayer(url: String, itemId: Long) {}
    override fun pausePlayer(url: String) {}
    override fun pauseAllPlayers() {}
    override fun resumeLastPlayed() {}
    override fun clearCacheById(itemId: Long?) {}
    override fun precacheVideos(urls: List<String>) {}
    override fun resetPool() {}
    override fun releaseAllPlayers() {}
}
