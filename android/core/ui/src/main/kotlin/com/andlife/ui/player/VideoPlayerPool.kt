package com.andlife.ui.player

import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import java.io.File

@OptIn(UnstableApi::class)
object VideoPlayerPool {
    private const val MAX_POOL_SIZE = 5
    private const val CACHED_SIZE = 300 * 1024 * 1024L // 300MB
//    private val videoPool = LinkedHashMap<String, VideoPlayer>(MAX_POOL_SIZE, 0.75f)
//    private var lastPlayedUri: String? = null
//    private val protectedUris = mutableSetOf<String>() // 보호할 URI 집합

    /*
    * MutableMap을 활용한 비디오 플레이어 풀 관리
    * */

    private val videoPool = mutableMapOf<String, VideoPlayer>()
    private val lastPlayedVideo =
        LinkedHashMap<Long, String>(MAX_POOL_SIZE, 0.75f, true) // accessOrder=true -> 최근 접근한 순서대로 정렬
    private var currentPlayingUri: String? = null

    private var simpleCache: SimpleCache? = null
    private var cacheDataSourceFactory: CacheDataSource.Factory? = null

    fun initializeCache(context: Context) {
        if (simpleCache != null) return

        val cacheDir = File(context.cacheDir, "video_cache") // 캐시 디렉토리 설정
        val databaseProvider = StandaloneDatabaseProvider(context) // 데이터베이스 제공자 생성
        val evictor = LeastRecentlyUsedCacheEvictor(CACHED_SIZE) // LRU 캐시 제거자 생성, LRU란: 가장 오랫동안 사용되지 않은 항목을 제거
        simpleCache = SimpleCache(cacheDir, evictor, databaseProvider) // SimpleCache 생성

        cacheDataSourceFactory = CacheDataSource.Factory()
            .setCache(simpleCache!!)
            .setUpstreamDataSourceFactory(DefaultDataSource.Factory(context))
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR) // 캐시 오류 무시 설정, 오류 발생 시 캐시를 무시하고 원본 데이터 소스에서 데이터를 가져옴

        Log.d("cachevvv", "비디오 캐시 초기화 완료")
    }

    fun getPlayer(context: Context, uri: String): VideoPlayer {
        if (cacheDataSourceFactory == null) {
            initializeCache(context)
            Log.d("cachevvv", "getPlayer: 캐시 데이터 소스 팩토리 초기화 완료")
        }

        simpleCache?.let { cache ->
            val cacheSpace = cache.cacheSpace
            val keys = cache.keys.toList()

            // 이 URI와 관련된 캐시 청크 찾기
            val cachedChunks = keys.filter { it.contains(uri.hashCode().toString()) }
            val cachedBytes = cachedChunks.sumOf { key ->
                cache.getCachedLength(key, 0, Long.MAX_VALUE)
            }

            Log.d("cachevvv", "=== 캐시 상태 ===")
            Log.d("cachevvv", "총 캐시 크기: ${cacheSpace / 1024 / 1024}MB")
            Log.d("cachevvv", "캐시된 파일 수: ${keys.size}개")
            Log.d("cachevvv", "URI: $uri")
            Log.d("cachevvv", "관련 캐시 청크: ${cachedChunks.size}개")
            Log.d("cachevvv", "캐시된 바이트: ${cachedBytes / 1024}KB")
            Log.d("cachevvv", "부분 캐시 여부: ${cachedChunks.isNotEmpty()}")
        }

        videoPool[uri]?.let { return it }

        // 플레이어 풀이 가득 찬 경우
        if (videoPool.size >= MAX_POOL_SIZE) {
            val protectedUris = lastPlayedVideo.values.toSet()

            val urlToRemove = videoPool.keys.find { it !in protectedUris }
                ?: lastPlayedVideo.entries.first()

            Log.d("vvv", "풀 가득 참. 제거하는 URI: $urlToRemove")
            videoPool.remove(urlToRemove)?.release()
        }

//        val exoPlayer = ExoPlayer.Builder(context).build().apply {
//            val mediaSource = ProgressiveMediaSource.Factory(
//                DefaultDataSource.Factory(context)
//            ).createMediaSource(MediaItem.fromUri(uri))
//            setMediaSource(mediaSource)
//            prepare()
//            repeatMode = Player.REPEAT_MODE_ONE
//        }

        val exoPlayer = ExoPlayer.Builder(context).build().apply {
            val mediaSource = ProgressiveMediaSource.Factory(
                cacheDataSourceFactory!!
            ).createMediaSource(MediaItem.fromUri(uri))
            setMediaSource(mediaSource)
            prepare()
            repeatMode = Player.REPEAT_MODE_ONE

            addAnalyticsListener(object : AnalyticsListener {
                override fun onBandwidthEstimate(
                    eventTime: AnalyticsListener.EventTime,
                    totalLoadTimeMs: Int,
                    totalBytesLoaded: Long,
                    bitrateEstimate: Long
                ) {
                    if (totalBytesLoaded > 0) {
                        Log.d("cachevvv", "네트워크에서 다운로드: ${totalBytesLoaded / 1024}KB")
                    } else {
                        Log.d("cachevvv", "캐시에서 로드!")
                    }
                }
            })
        }

        val newPlayer = VideoPlayer(exoPlayer, uri)
        videoPool[uri] = newPlayer

        if (uri == currentPlayingUri) {
            newPlayer.play()
            Log.d("vvv", "재생 중이던 URI의 플레이어 재사용: $uri")
        }

        Log.d("cachevvv", "getPlayer: 새 플레이어 생성 및 캐시 사용 중인 URI: $uri")
        Log.d("cachevvv", "현재 캐시 사용 중인 URI들: ${videoPool.keys}")

        return newPlayer
    }

    fun playPlayer(context: Context, uri: String, guestBookId: Long) {
        currentPlayingUri = uri // 현재 재생 중인 URI 업데이트

        videoPool.values.forEach {
            if (it.uri != uri) {
                it.pause() // 다른 플레이어 일시정지
            }
        }

        lastPlayedVideo.remove(guestBookId) // 기존에 있던 항목 제거
        lastPlayedVideo[guestBookId] = uri // 최근 재생한 순서로 갱신

        if (lastPlayedVideo.size > MAX_POOL_SIZE) { // 최대 크기 초과 시 가장 오래된 항목 제거
            val oldestKey = lastPlayedVideo.keys.first() // 가장 오래된 키 가져오기
            lastPlayedVideo.remove(oldestKey)
        }

        val player = videoPool[uri] ?: getPlayer(context, uri) // 플레이어가 없으면 새로 생성
        player.play()

        Log.d("vvv", "재생하는 URI: $uri")
        Log.d("vvv", "최근 재생된 URI들: ${lastPlayedVideo.values}")
    }

    fun pausePlayer(uri: String) {
        if (currentPlayingUri == uri) currentPlayingUri = null
        videoPool[uri]?.pause()
    }

    fun pauseAllPlayers() {
        videoPool.values.forEach { it.pause() }
    }

    fun resumeLastPlayed() {
        currentPlayingUri?.let { videoPool[it]?.play() }
    }

    fun preparePlayer(context: Context, uri: String) {
        if (videoPool.containsKey(uri)) return
        // 풀이 거의 가득 찬 경우 미리 준비하지 않음 (메모리 절약)
        if (videoPool.size >= MAX_POOL_SIZE - 2) return
        getPlayer(context, uri)
    }

    fun releaseAll() {
        videoPool.values.forEach { it.release() }
        videoPool.clear()
        lastPlayedVideo.clear()
        currentPlayingUri = null

        simpleCache?.release()
        simpleCache = null
        cacheDataSourceFactory = null
        Log.d("cachevvv", "모든 비디오 플레이어 및 캐시 해제 완료")
    }
}
