package com.andlife.media.video

import android.app.ActivityManager
import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.CacheKeyFactory
import androidx.media3.datasource.cache.CacheWriter
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.andlife.media.di.VideoCacheDataSourceFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

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

    private val _isMuted = MutableStateFlow(false)
    override val isMuted = _isMuted.asStateFlow()

    private val precacheScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val activePrecacheJobs = ConcurrentHashMap<String, Job>()

    @OptIn(UnstableApi::class)
    override fun preparePlayers(neededCount: Int) {
        val targetSize = minOf(playerInstances.size + neededCount, maxPoolSize)
        while (playerInstances.size < targetSize) {
            playerInstances.add(createNewPlayer())
        }
    }

    @OptIn(UnstableApi::class)
    private fun createNewPlayer(): AutoVideoPlayer {
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                15_000,
                30_000,
                1_500,
                2_500
            )
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()

        val exoPlayer =
            ExoPlayer.Builder(context).setLoadControl(loadControl).build().apply {
                repeatMode = ExoPlayer.REPEAT_MODE_ONE
            }

        val playerView = PlayerView(context).apply {
            useController = false
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            player = exoPlayer
            setBackgroundColor(Color.BLACK)
        }

        return AutoVideoPlayer(exoPlayer, playerView, "")
    }

    @OptIn(UnstableApi::class)
    override fun getPlayer(url: String): AutoVideoPlayer {
        if (playerInstances.isEmpty()) preparePlayers(maxPoolSize)
        activePlayers[url]?.let { existingPlayer ->
            if (existingPlayer.exoPlayer.currentMediaItem?.localConfiguration?.uri.toString() == url) {
                return existingPlayer
            }
            return existingPlayer
        }
        if (playerInstances.isEmpty()) {
            preparePlayers(maxPoolSize)
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
        playerToUse.setMuted(_isMuted.value)
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

    override fun toggleMute() {
        _isMuted.update { !it }
        activePlayers.values.forEach { it.toggleMute(_isMuted.value) }
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

            val cacheBytes = cache.getCachedBytes(
                CacheKeyFactory.DEFAULT.buildCacheKey(DataSpec(uri)),
                0,
                PRECACHE_SIZE_BYTES
            )

            if (cacheBytes >= PRECACHE_SIZE_BYTES) return@forEach

            val precacheJob = precacheScope.launch(start = CoroutineStart.LAZY) {
                var cancellationHandle: DisposableHandle? = null
                try {
                    val dataSpec = DataSpec.Builder()
                        .setUri(uri)
                        .setLength(PRECACHE_SIZE_BYTES)
                        .setFlags(DataSpec.FLAG_ALLOW_CACHE_FRAGMENTATION)
                        .build()

                    val cacheWriter = CacheWriter(
                        cacheDataSourceFactory.createDataSourceForDownloading(),
                        dataSpec,
                        null
                    ) { _, _, _ ->
                        ensureActive()
                    }
                    cancellationHandle = coroutineContext.job.invokeOnCompletion { cause ->
                        if (cause is CancellationException) {
                            cacheWriter.cancel()
                        }
                    }
                    cacheWriter.cache()
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.e(TAG, "프리캐시 실패: $url", e)
                } finally {
                    cancellationHandle?.dispose()
                    activePrecacheJobs.remove(url)
                }
            }

            val existing = activePrecacheJobs.putIfAbsent(url, precacheJob)
            if (existing != null) {
                precacheJob.cancel()
                return@forEach
            }

            precacheJob.start()
        }
    }

    override fun resetPool() {
        activePlayers.values.forEach { it.stop() }
        activePlayers.clear()
        lastPlayedUrlByGuestBookId.clear()
        currentPlayingUrl = null
    }

    override fun releaseAllPlayers() {
        activePrecacheJobs.values.forEach { job ->
            job.cancel(CancellationException("Pool released"))
        }
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
            totalMemoryGb >= 7.0 -> 3
            totalMemoryGb >= 5.0 -> 3
            else -> 2
        }
    }

    companion object {
        private const val TAG = "AutoVideoPlayerPool"
        private const val PRECACHE_SIZE_BYTES = 1 * 1024 * 1024L
    }
}
