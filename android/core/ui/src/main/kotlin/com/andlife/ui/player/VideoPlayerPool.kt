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
    private const val CACHED_SIZE = 300 * 1024 * 1024L

    private val playerInstances = mutableListOf<VideoPlayer>() // 실제 ExoPlayer 인스턴스들을 보관하는 리스트
    private val activePlayers = mutableMapOf<String, VideoPlayer>() // 현재 사용 중인(메모리에 올라와 있는) URI와 VideoPlayer 매핑

    // 보호 기능: 방명록 id 별로 마지막 재생 url을 저장(세로 스크롤 보호용)
    private val lastPlayedUriByGuestBookId =
        LinkedHashMap<Long, String>(MAX_POOL_SIZE, 0.75f, true) // 0.75f 로드 팩터, true 접근 순서 기반.

    private var currentPlayingUri: String? = null
    private var simpleCache: SimpleCache? = null
    private var cacheDataSourceFactory: CacheDataSource.Factory? = null

    /*
     * 화면 진입 전에 미리 플레이어를 준비하는 메서드
     * */
    fun preparePlayers(context: Context) {
        if (playerInstances.isNotEmpty()) return // 이미 준비된 플레이어가 있으면 종료
        initializeCache(context)
        repeat(MAX_POOL_SIZE) {
            val exoPlayer = ExoPlayer.Builder(context).build().apply {
                repeatMode = Player.REPEAT_MODE_ONE
            }
            playerInstances.add(VideoPlayer(exoPlayer, ""))
        }
    }

    /*
     * 캐시 초기화 메서드
     * */
    private fun initializeCache(context: Context) {
        if (simpleCache != null) return

        val cacheDir = File(context.cacheDir, "video_cache") // TODO: 캐시 디렉토리 이름 수정 고려
        val databaseProvider = StandaloneDatabaseProvider(context)
        val evictor = LeastRecentlyUsedCacheEvictor(CACHED_SIZE)
        simpleCache = SimpleCache(cacheDir, evictor, databaseProvider)

        cacheDataSourceFactory =
            CacheDataSource
                .Factory()
                .setCache(simpleCache!!)
                .setUpstreamDataSourceFactory(DefaultDataSource.Factory(context))
                .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR) // 캐시 오류 무시 설정, 오류 발생 시 캐시를 무시하고 원본 데이터 소스에서 데이터를 가져옴

        Log.d("cachevvv", "비디오 캐시 초기화 완료")
    }

    fun getPlayer(uri: String): VideoPlayer {
        activePlayers[uri]?.let { return it }

        val playerToUse = if (activePlayers.size < playerInstances.size) {
            // 아직 풀에 여유가 있으면 리스트에서 미사용 중인 플레이어를 가져옴
            playerInstances[activePlayers.size]
        } else {
            // 풀이 가득 찼으면 가장 오래된 플레이어를 제거하고 재사용
            val protectedUris = lastPlayedUriByGuestBookId.values.toSet()
            val playerToRemoveUri = activePlayers.keys.firstOrNull { it !in protectedUris }
                ?: activePlayers.entries.first() // 보호된 URI가 아닌 것 중 첫 번째, 없으면 그냥 첫 번째

            val removedPlayer = activePlayers.remove(playerToRemoveUri)!! // 플레이어 제거
            removedPlayer.stop() // 상태 패턴에 의해 내부 exoPlayer.stop() 및 상태 변경 발생
            removedPlayer // 재사용을 위해 반환
        }

        //playerToUse.uri = uri
        val updatedPlayer = VideoPlayer(playerToUse.exoPlayer, uri) // class의 멤버변수인 uri를 var로 해도 되나? 일단 안정성을 위해 새로 생성
        val mediaSource = ProgressiveMediaSource
            .Factory(cacheDataSourceFactory!!)
            .createMediaSource(MediaItem.fromUri(uri))

        updatedPlayer.setMediaSource(mediaSource)
        updatedPlayer.prepare()
        activePlayers[uri] = updatedPlayer
        return updatedPlayer
    }

    fun playPlayer(uri: String, guestBookId: Long) {
        currentPlayingUri = uri // 현재 재생 중인 URI 업데이트

        // 방명록 id 별로 마지막 재생 URI 저장
        lastPlayedUriByGuestBookId[guestBookId] = uri
        if (lastPlayedUriByGuestBookId.size > MAX_POOL_SIZE) {// 최대 크기 초과 시 가장 오래된 항목 제거
            lastPlayedUriByGuestBookId.remove(lastPlayedUriByGuestBookId.keys.first())
        }

        activePlayers.values.toList().forEach { // toList()로 복사본 생성하여 수정 중 예외 방지
            if (it.uri != uri) {
                it.pause() // 다른 플레이어 일시정지
            }
        }

        val player = getPlayer(uri)
        player.play()

        Log.d("rere", "재생: $uri (방명록 id: $guestBookId)")
        Log.d("rerere", "보호 중인 URI 목록: ${lastPlayedUriByGuestBookId.values}")
        Log.d("rererere", "활성 플레이어 목록: ${activePlayers.keys}")
    }

    fun pausePlayer(uri: String) {
        if (currentPlayingUri == uri) currentPlayingUri = null
        activePlayers[uri]?.pause()
    }

    fun pauseAllPlayers() {
        activePlayers.values.forEach { it.pause() }
    }

    fun resumeLastPlayed() {
        currentPlayingUri?.let { activePlayers[it]?.play() }
    }

    fun preparePlayer(
        context: Context,
        uri: String,
    ) {
        if (activePlayers.containsKey(uri)) return
        if (activePlayers.size >= MAX_POOL_SIZE - 2) return
        getPlayer(uri)
    }

    fun releaseAll() {
        activePlayers.values.forEach { it.release() }
        activePlayers.clear()
        playerInstances.forEach { it.release() }
        playerInstances.clear()
        currentPlayingUri = null

        simpleCache?.release()
        simpleCache = null
        cacheDataSourceFactory = null
        Log.d("cachevvv", "모든 비디오 플레이어 및 캐시 해제 완료")
    }
}
