package com.andlife.media

import android.app.ActivityManager
import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.CacheWriter
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import javax.inject.Inject
import androidx.core.net.toUri
import androidx.media3.exoplayer.DefaultLoadControl
import com.andlife.media.di.StoryCacheDataSourceFactory
import com.andlife.media.di.StorySimpleCache
import java.util.concurrent.ConcurrentHashMap

@OptIn(UnstableApi::class)
class StoryMediaPlayerPoolImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @StorySimpleCache private val storyCache: Cache,
    @StoryCacheDataSourceFactory private val cacheDataSourceFactory: CacheDataSource.Factory
) : StoryMediaPlayerPool {

    private val precacheScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val activePrecacheJobs = ConcurrentHashMap<String, Job>()

    private val playerMap = object : LinkedHashMap<Int, StoryMediaPlayer>(
        getDynamicPoolSize(), 0.75f, true
    ) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<Int, StoryMediaPlayer>?): Boolean {
            if (size > getDynamicPoolSize()) {
                eldest?.value?.let { player ->
                    try {
                        player.release()
                    } catch (e: Exception) {
                        Log.e(TAG, "Release failed during eviction: ${e.message}")
                    }
                }
                return true
            }
            return false
        }
    }

    private fun getDynamicPoolSize(): Int {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val info = ActivityManager.MemoryInfo()
        am.getMemoryInfo(info)
        val totalGb = info.totalMem.toDouble() / (1024 * 1024 * 1024)
        return if (totalGb >= 8.0) 3 else 2
    }

    override fun acquirePlayer(index: Int, url: String): StoryMediaPlayer {
        playerMap[index]?.let { return it }

        // 최대 버퍼 사이즈 2초로 제한
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                1000,
                2000,
                1000,
                1000
            )
            .build()

        val exoPlayer = ExoPlayer.Builder(context)
            .setLoadControl(loadControl)
            .build().apply {
                val mediaSource = ProgressiveMediaSource.Factory(cacheDataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(url))
                setMediaSource(mediaSource)
                repeatMode = Player.REPEAT_MODE_ONE
            }

        return StoryMediaPlayer(exoPlayer).also {
            playerMap[index] = it
        }
    }

    override fun play(index: Int) {
        playerMap.forEach { (idx, player) ->
            if (idx == index) {
                if (player.playbackState == Player.STATE_IDLE) {
                    player.prepare()
                }
                player.play()
            } else {
                player.pause()
            }
        }
    }

    override fun pause(index: Int) {
        playerMap[index]?.pause()
    }

    override fun pauseAll() {
        playerMap.values.forEach { it.pause() }
    }

    override fun resumeLastPlayed(index: Int) {
        playerMap[index]?.let { player ->
            if (player.playbackState == Player.STATE_IDLE) {
                player.prepare()
            }
            player.play()
        }
    }

    override fun resetPool() {
        playerMap.values.forEach { it.stop() }
        playerMap.clear()
    }

    override fun releaseAll() {
        precacheScope.cancel()
        activePrecacheJobs.clear()
        playerMap.values.forEach { it.release() }
        playerMap.clear()
    }

    @OptIn(UnstableApi::class)
    override fun precacheVideos(urls: List<String>) {
        urls.forEach { url ->
            if (activePrecacheJobs.containsKey(url)) return@forEach

            // 이미 캐시가 존재하는지 체크하여 불필요한 요청 방지
            val cached = storyCache.getCachedBytes(url, 0, PRECACHE_SIZE)
            if (cached >= PRECACHE_SIZE) return@forEach

            activePrecacheJobs[url] = precacheScope.launch {
                try {
                    val dataSpec = DataSpec.Builder()
                        .setUri(url.toUri())
                        .setLength(PRECACHE_SIZE)
                        .setFlags(DataSpec.FLAG_ALLOW_CACHE_FRAGMENTATION)
                        .build()

                    CacheWriter(
                        cacheDataSourceFactory.createDataSourceForDownloading(),
                        dataSpec,
                        null
                    ) { _, _, _ ->
                        if (!isActive) throw CancellationException()
                    }.cache()

                } catch (e: Exception) {
                    if (e !is CancellationException) {
                        Log.e(TAG, "Precache failed: ${url.takeLast(20)} | ${e.message}")
                    }
                } finally {
                    activePrecacheJobs.remove(url)
                }
            }
        }
    }

    companion object {
        private const val TAG = "StoryPlayerPool"
        const val PRECACHE_SIZE = 1 * 1024 * 1024L // 1MB
    }
}
