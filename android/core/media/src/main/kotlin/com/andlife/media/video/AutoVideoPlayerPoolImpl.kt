package com.andlife.media.video

import android.app.ActivityManager
import android.content.Context
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.CacheKeyFactory
import androidx.media3.datasource.cache.CacheWriter
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.andlife.media.di.VideoCacheDataSourceFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class AutoVideoPlayerPoolImpl @UnstableApi @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @param:VideoCacheDataSourceFactory private val cacheDataSourceFactory: CacheDataSource.Factory,
) : AutoVideoPlayerPool {

    private val maxPoolSize: Int by lazy { getDynamicPoolSize() }
    private val playerInstances = mutableListOf<AutoVideoPlayer>() // 재사용 가능한 플레이어 인스턴스 풀
    private val activePlayers = mutableMapOf<String, AutoVideoPlayer>() // 현재 사용 중인 플레이어 매핑
    private val lastPlayedUrlByGuestBookId =
        LinkedHashMap<Long, String>(maxPoolSize, 0.75f, true) // 방명록 ID별 마지막 재생 URL 추적

    private var currentPlayingUrl: String? = null

    private val precacheScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val activePrecacheJobs = ConcurrentHashMap<String, Job>()

    init {
        preparePlayers()
    }

    @OptIn(UnstableApi::class)
    override fun preparePlayers() {
        if (playerInstances.isNotEmpty()) return

        repeat(maxPoolSize) {
            val exoPlayer =
                ExoPlayer.Builder(context).build().apply {
                    repeatMode = ExoPlayer.REPEAT_MODE_ONE
                }

            val playerView = PlayerView(context).apply {
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                player = exoPlayer
                setBackgroundColor(android.graphics.Color.BLACK)
            }

            playerInstances.add(AutoVideoPlayer(exoPlayer, playerView, ""))
        }
    }

    @OptIn(UnstableApi::class)
    override fun getPlayer(url: String): AutoVideoPlayer {
        if (playerInstances.isEmpty()) preparePlayers()
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

        playerToUse.setMediaSource(mediaSource)
        playerToUse.prepare()
        activePlayers[url] = playerToUse
        return playerToUse
    }

    override fun playPlayer(
        url: String,
        itemId: Long,
    ) {
        currentPlayingUrl = url
        lastPlayedUrlByGuestBookId[itemId] = url

        if (lastPlayedUrlByGuestBookId.size > maxPoolSize) {
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

    override fun clearCacheById(itemId: Long?) {
        if (itemId == null) return
        val oldUrl = lastPlayedUrlByGuestBookId[itemId]
        if (oldUrl != null) {
            activePlayers[oldUrl]?.stop()
            activePlayers.remove(oldUrl)
            if (currentPlayingUrl == oldUrl) currentPlayingUrl = null
        }

        lastPlayedUrlByGuestBookId.remove(itemId)
    }

    @OptIn(UnstableApi::class)
    override fun precacheVideos(urls: List<String>) {
        val cache = cacheDataSourceFactory.cache ?: return

        urls.forEach { url ->
            val uri = url.toUri()

            if (activePrecacheJobs.contains(url)) return@forEach

            val cacheBytes = cache.getCachedBytes(
                CacheKeyFactory.DEFAULT.buildCacheKey(DataSpec(uri)),
                0,
                PRECACHE_SIZE_BYTES
            )

            if (cacheBytes >= PRECACHE_SIZE_BYTES) return@forEach

            activePrecacheJobs[url] = precacheScope.launch {
                try {
                    val dataSpec = DataSpec.Builder()
                        .setUri(uri)
                        .setLength(PRECACHE_SIZE_BYTES)
                        .setFlags(DataSpec.FLAG_ALLOW_CACHE_FRAGMENTATION)
                        .build()

                    val cacheWriter = CacheWriter(
                        cacheDataSourceFactory.createDataSourceForDownloading(),
                        dataSpec,
                        null,
                        null
                    )
                    cacheWriter.cache()
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    activePrecacheJobs.remove(url)
                }
            }
        }
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
        currentPlayingUrl = null
    }

    private fun getDynamicPoolSize(): Int {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        val totalMemoryGb = memoryInfo.totalMem.toDouble() / (1024 * 1024 * 1024)

        return when {
            totalMemoryGb >= 7.0 -> 4
            totalMemoryGb >= 5.0 -> 3
            else -> 2
        }
    }

    companion object {
        private const val PRECACHE_SIZE_BYTES = 1 * 1024 * 1024L
    }
}
