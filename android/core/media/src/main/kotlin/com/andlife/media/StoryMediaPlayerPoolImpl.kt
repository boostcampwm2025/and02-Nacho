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
import com.andlife.media.di.StoryCacheDataSourceFactory
import com.andlife.media.di.StorySimpleCache

@OptIn(UnstableApi::class)
class StoryMediaPlayerPoolImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @StorySimpleCache private val storyCache: Cache,
    @StoryCacheDataSourceFactory private val cacheDataSourceFactory: CacheDataSource.Factory
) : StoryMediaPlayerPool {

    private val precacheScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val activePrecacheJobs = mutableMapOf<String, Job>()

    private val playerMap = object : LinkedHashMap<Int, StoryMediaPlayer>(
        getDynamicPoolSize(), 0.75f, true
    ) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<Int, StoryMediaPlayer>?): Boolean {
            if (size > getDynamicPoolSize()) {
                eldest?.value?.release() // 풀 사이즈 초과 시 가장 오래된 플레이어 해제
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
        return if (totalGb >= 8.0) 5 else 3
    }

    override fun acquirePlayer(index: Int, url: String): StoryMediaPlayer {
        playerMap[index]?.let { return it }

        // 새 플레이어 생성
        val exoPlayer = ExoPlayer.Builder(context).build().apply {
            val mediaSource = ProgressiveMediaSource.Factory(cacheDataSourceFactory)
                .createMediaSource(MediaItem.fromUri(url))
            setMediaSource(mediaSource)
            repeatMode = Player.REPEAT_MODE_ONE
            prepare()
        }

        val storyPlayer = StoryMediaPlayer(exoPlayer)
        playerMap[index] = storyPlayer
        return storyPlayer
    }

    override fun precacheVideos(urls: List<String>) {
        urls.forEach { url ->
            if (activePrecacheJobs.containsKey(url)) return@forEach

            // 이미 1MB 이상 캐싱되었는지 확인
            val cached = storyCache.getCachedBytes(url, 0, PRECACHE_SIZE)
            if (cached >= PRECACHE_SIZE) return@forEach

            activePrecacheJobs[url] = precacheScope.launch {
                try {
                    CacheWriter(
                        cacheDataSourceFactory.createDataSourceForDownloading(),
                        DataSpec.Builder().setUri(url.toUri()).setLength(PRECACHE_SIZE).build(),
                        null, null
                    ).cache()
                } catch (e: Exception) {
                    Log.d("StoryMediaPlayerPool", "Error precaching video: $url")
                } finally {
                    activePrecacheJobs.remove(url)
                }
            }
        }
    }

    override fun play(index: Int) {
        playerMap.forEach { (idx, player) ->
            if (idx == index) player.play() else player.pause()
        }
    }

    override fun pause(index: Int) {
        playerMap[index]?.pause()
    }

    override fun pauseAll() {
        playerMap.values.forEach { it.pause() }
    }

    override fun resumeLastPlayed(index: Int) {
        playerMap[index]?.play()
    }

    override fun resetPool() {
        playerMap.values.forEach { it.stop() }
        playerMap.clear()
    }

    override fun releaseAll() {
        precacheScope.cancel()
        playerMap.values.forEach { it.release() }
        playerMap.clear()
    }

    companion object {
        const val PRECACHE_SIZE = 1 * 1024 * 1024L // 1MB
    }
}
