package com.andlife.media.video

import android.content.Context
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.CacheKeyFactory
import androidx.media3.datasource.cache.CacheWriter
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
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
    private val cacheDataSourceFactory: CacheDataSource.Factory,
) : AutoVideoPlayerPool {

    private val playerInstances = mutableListOf<AutoVideoPlayer>() // 재사용 가능한 플레이어 인스턴스 풀
    private val activePlayers = mutableMapOf<String, AutoVideoPlayer>() // 현재 사용 중인 플레이어 매핑
    private val lastPlayedUrlByGuestBookId =
        LinkedHashMap<Long, String>(MAX_POOL_SIZE, 0.75f, true) // 방명록 ID별 마지막 재생 URL 추적

    private var currentPlayingUrl: String? = null

    // 프리캐싱을 위한 코루틴 스코프
    // SupervisorJob을 사용하여 하나의 작업 실패가 전체 스코프에 영향을 미치지 않도록 함 -> 하나의 비디오 프리캐싱 실패가 다른 작업에 영향 X
    private val precacheScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // 현재 프리캐싱 중인 작업들을 추적하기 위한 맵
    // 왜 ConcurrentHashMap을 사용하는가? -> 멀티스레드 환경에서 안전하게 접근하기 위해
    // 예를 들어, 여러 비디오 url이 동시에 프리캐싱 요청될 때, 여러 스레드가 이 맵에 접근할 수 있기 때문
    // 이를 통해 race condition이나 데이터 불일치 문제를 방지할 수 있음
    // 위에서 precacheScope가 여러 코루틴을 동시에 실행할 수 있기 때문에, 이 맵도 멀티스레드 환경에서 안전해야 함
    private val activePrecacheJobs = ConcurrentHashMap<String, Job>()

    init {
        preparePlayers()
    }

    @OptIn(UnstableApi::class)
    override fun preparePlayers() {
        if (playerInstances.isNotEmpty()) return
        repeat(MAX_POOL_SIZE) {
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
        Log.d("wwwwww", "프리캐싱 요청됨: $urls")
        val cache = cacheDataSourceFactory.cache ?: return // 캐시가 없으면 프리캐싱 불가
        Log.d("wwwwww", "캐시 존재, 프리캐싱 시작: $urls")

        urls.forEach { url ->
            val uri = url.toUri()

            if (activePrecacheJobs.contains(url)) {
                Log.d("wwwwww", "이미 프리캐싱 중: $url")
                return@forEach // 이미 프리캐싱 중인 경우 건너뜀
            }

            val cacheBytes = cache.getCachedBytes( // 이미 캐시된 바이트 수 확인
                CacheKeyFactory.DEFAULT.buildCacheKey(DataSpec(uri)),
                0,
                PRECACHE_SIZE_BYTES
            )

            if (cacheBytes >= PRECACHE_SIZE_BYTES) {
                Log.d("wwwwww", "이미 캐시 완료됨: $url")
                return@forEach // 이미 충분히 캐시된 경우 건너뜀
            }

            Log.d("wwwwww", "프리캐싱 시작 필요: $url, 이미 캐시된 바이트: $cacheBytes")

            activePrecacheJobs[url] = precacheScope.launch { // url이 여러 개면 각각의 코루틴에서 프리캐싱 작업 수행 -> 그래서 ConcurrentHashMap을 사용하는 것
                Log.d("wwwwww", "이전에 캐시되지 않은 비디오 프리캐싱 시작: $url")
                try {
                    val dataSpec = DataSpec.Builder()
                        .setUri(uri)
                        .setLength(PRECACHE_SIZE_BYTES)
                        .setFlags(DataSpec.FLAG_ALLOW_CACHE_FRAGMENTATION) // 조각화된 캐시 허용
                        .build()

                    val cacheWriter = CacheWriter(
                        cacheDataSourceFactory.createDataSourceForDownloading(), // 다운로드 전용 데이터 소스
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

        Log.d("wwwwww", "프리캐싱 작업 개수: ${activePrecacheJobs.size}")
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

    companion object {
        private const val MAX_POOL_SIZE = 5
        private const val PRECACHE_SIZE_BYTES = 1 * 1024 * 1024L
    }
}
