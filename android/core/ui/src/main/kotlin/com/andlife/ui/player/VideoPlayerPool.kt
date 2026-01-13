package com.andlife.ui.player

import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource

@OptIn(UnstableApi::class)
object VideoPlayerPool {
    private const val MAX_POOL_SIZE = 5
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

    fun getPlayer(context: Context, uri: String): VideoPlayer {
        videoPool[uri]?.let { return it }

        // 플레이어 풀이 가득 찬 경우
        if (videoPool.size >= MAX_POOL_SIZE) {
            val protectedUris = lastPlayedVideo.values.toSet()

            val urlToRemove = videoPool.keys.find { it !in protectedUris }
                ?: lastPlayedVideo.entries.first()

            Log.d("vvv", "풀 가득 참. 제거하는 URI: $urlToRemove")
            videoPool.remove(urlToRemove)?.release()
        }

        val exoPlayer = ExoPlayer.Builder(context).build().apply {
            val mediaSource = ProgressiveMediaSource.Factory(
                DefaultDataSource.Factory(context)
            ).createMediaSource(MediaItem.fromUri(uri))
            setMediaSource(mediaSource)
            prepare()
            repeatMode = Player.REPEAT_MODE_ONE
        }

        val newPlayer = VideoPlayer(exoPlayer, uri)
        videoPool[uri] = newPlayer

        if (uri == currentPlayingUri) {
            newPlayer.play()
            Log.d("vvv", "재생 중이던 URI의 플레이어 재사용: $uri")
        }

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
//        videoPool.values.forEach { it.release() }
//        videoPool.clear()
//        protectedUris.clear()
        videoPool.values.forEach { it.release() }
        videoPool.clear()
        lastPlayedVideo.clear()
        currentPlayingUri = null
    }
}
