package com.andlife.media.video

import android.content.Context
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AutoVideoPlayerPoolImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val simpleCache: Cache,
) : AutoVideoPlayerPool {

    private val playerInstances = mutableListOf<AutoVideoPlayer>() // 재사용 가능한 플레이어 인스턴스 풀
    private val activePlayers = mutableMapOf<String, AutoVideoPlayer>() // 현재 사용 중인 플레이어 매핑
    private val lastPlayedUrlByGuestBookId = LinkedHashMap<Long, String>(MAX_POOL_SIZE, 0.75f, true) // 방명록 ID별 마지막 재생 URL 추적

    private var currentPlayingUrl: String? = null
    private val cacheDataSourceFactory: CacheDataSource.Factory by lazy {
        CacheDataSource.Factory()
            .setCache(simpleCache)
            .setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory())
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }

    init {
        preparePlayers()
    }

    override fun preparePlayers() {
        if (playerInstances.isNotEmpty()) return
        repeat(MAX_POOL_SIZE) {
            val exoPlayer = ExoPlayer.Builder(context).build().apply {
                repeatMode = ExoPlayer.REPEAT_MODE_ONE
            }
            playerInstances.add(AutoVideoPlayer(exoPlayer, ""))
        }
    }

    override fun getPlayer(url: String): AutoVideoPlayer {
        activePlayers[url]?.let { return it }

        val playerToUse = if (activePlayers.size < playerInstances.size) {
            playerInstances[activePlayers.size]
        } else {
            val protectedUris = lastPlayedUrlByGuestBookId.values.toSet()

            val playerToRemoveUri = activePlayers.keys.firstOrNull { it !in protectedUris }
                ?: activePlayers.keys.firstOrNull()

            if (playerToRemoveUri != null) {
                val removedPlayer = activePlayers.remove(playerToRemoveUri)!!
                removedPlayer.stop()
                removedPlayer
            } else {
                playerInstances.first()
            }
        }

        playerToUse.url = url
        val mediaSource = ProgressiveMediaSource
            .Factory(cacheDataSourceFactory)
            .createMediaSource(MediaItem.fromUri(url))

        playerToUse.exoPlayer.setMediaSource(mediaSource)
        activePlayers[url] = playerToUse
        return playerToUse
    }

    override fun playPlayer(url: String, itemId: Long) {
        currentPlayingUrl = url
        lastPlayedUrlByGuestBookId[itemId] = url

        if (lastPlayedUrlByGuestBookId.size > MAX_POOL_SIZE) {
            lastPlayedUrlByGuestBookId.remove(lastPlayedUrlByGuestBookId.keys.first())
        }

        activePlayers.values.toList().forEach { // toList()로 복사본 생성하여 수정 중 예외 방지
            if (it.url != url) {
                it.pause()
            }
        }

        val player = getPlayer(url)
        player.play()

        Log.d("rere", "재생: $url (방명록 id: $itemId)")
        Log.d("rerere", "보호 중인 URI 목록: ${lastPlayedUrlByGuestBookId.values}")
        Log.d("rererere", "활성 플레이어 목록: ${activePlayers.keys}")
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

    override fun resetPool() { // 추후 상세 화면의 방명록 탭으로 진입 시 호출하면 될 것 같음.
        activePlayers.values.forEach { it.stop() } // 모든 활성 플레이어 정지
        activePlayers.clear() // 활성 플레이어 매핑 초기화
        lastPlayedUrlByGuestBookId.clear() // 마지막 재생 URL 기록 초기화
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
