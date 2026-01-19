package com.andlife.media.video

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AutoVideoPlayerPoolImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val cacheDataSourceFactory: CacheDataSource.Factory,
) : AutoVideoPlayerPool {

    private val playerInstances = mutableListOf<AutoVideoPlayer>() // 재사용 가능한 플레이어 인스턴스 풀
    private val activePlayers = mutableMapOf<String, AutoVideoPlayer>() // 현재 사용 중인 플레이어 매핑
    private val lastPlayedUrlByGuestBookId =
        LinkedHashMap<Long, String>(MAX_POOL_SIZE, 0.75f, true) // 방명록 ID별 마지막 재생 URL 추적

    private var currentPlayingUrl: String? = null

    init {
        preparePlayers()
    }

    override fun preparePlayers() {
        if (playerInstances.isNotEmpty()) return
        repeat(MAX_POOL_SIZE) {
            val exoPlayer =
                ExoPlayer.Builder(context).build().apply {
                    repeatMode = ExoPlayer.REPEAT_MODE_ONE
                }
            playerInstances.add(AutoVideoPlayer(exoPlayer, ""))
        }
    }

    override fun getPlayer(url: String): AutoVideoPlayer {
        if (playerInstances.isEmpty()) {
            preparePlayers()
        }
        activePlayers[url]?.let { existingPlayer ->
            if (existingPlayer.exoPlayer.currentMediaItem?.localConfiguration?.uri.toString() == url) {
                return existingPlayer
            }
            return existingPlayer
        }
        if (playerInstances.isEmpty()) {
            preparePlayers()
        }

        val playerToUse =
            if (activePlayers.size < playerInstances.size) {
                playerInstances[activePlayers.size]
            } else {
                val protectedUrls = lastPlayedUrlByGuestBookId.values.toSet()

                val playerToRemoveUrl =
                    activePlayers.keys.firstOrNull { it !in protectedUrls }
                        ?: activePlayers.keys.firstOrNull()

                if (playerToRemoveUrl != null) {
                    val removedPlayer = activePlayers.remove(playerToRemoveUrl)!!
                    removedPlayer.stop()
                    removedPlayer
                } else {
                    playerInstances.first()
                }
            }

        playerToUse.url = url
        val mediaSource =
            ProgressiveMediaSource
                .Factory(cacheDataSourceFactory)
                .createMediaSource(MediaItem.fromUri(url))

        playerToUse.exoPlayer.setMediaSource(mediaSource)
        activePlayers[url] = playerToUse
        return playerToUse
    }

    override fun playPlayer(
        url: String,
        itemId: Long,
    ) {
        currentPlayingUrl = url
        lastPlayedUrlByGuestBookId[itemId] = url

        if (lastPlayedUrlByGuestBookId.size > MAX_POOL_SIZE) {
            lastPlayedUrlByGuestBookId.remove(lastPlayedUrlByGuestBookId.keys.first())
        }

        activePlayers.values.toList().forEach {
            if (it.url != url) {
                it.pause()
            }
        }

        val player = getPlayer(url)
        player.play()
    }

    override fun pausePlayer(url: String) {
        if (currentPlayingUrl == url) currentPlayingUrl = null
        activePlayers[url]?.pause()
    }

    override fun pauseAllPlayers() {
        activePlayers.values.forEach { it.pause() }
    }

    override fun resumeLastPlayed() {
        currentPlayingUrl?.let { activePlayers[it]?.play() }
    }

    override fun resetPool() {
        activePlayers.values.forEach { it.stop() }
        activePlayers.clear()
        lastPlayedUrlByGuestBookId.clear()
        currentPlayingUrl = null
    }

    override fun releaseAllPlayers() {
        playerInstances.forEach { it.release() }
        playerInstances.clear()
        activePlayers.values.forEach { it.release() }
        activePlayers.clear()
        lastPlayedUrlByGuestBookId.clear()
    }

    companion object {
        private const val MAX_POOL_SIZE = 5
    }
}
