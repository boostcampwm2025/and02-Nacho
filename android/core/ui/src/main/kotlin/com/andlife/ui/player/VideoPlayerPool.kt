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
    private const val MAX_POOL_SIZE = 10
    private val videoPool = LinkedHashMap<String, VideoPlayer>(MAX_POOL_SIZE, 0.75f)
    private var lastPlayedUri: String? = null
    private val protectedUris = mutableSetOf<String>() // 보호할 URI 집합

    // 현재 재생 중이거나 화면에 보이는 플레이어 보호
    fun protectPlayer(uri: String) {
        protectedUris.add(uri)
        Log.d("eee", "보호된 URI들: $protectedUris")
    }

    fun unprotectPlayer(uri: String) {
        protectedUris.remove(uri)
        Log.d("eee", "보호 X된 URI: $uri")
        Log.d("eee", "보호된 URI들: $protectedUris")
    }

    fun getPlayer(context: Context, uri: String): VideoPlayer {
        videoPool[uri]?.let { return it }

        val reusablePlayer = videoPool.values.find { player ->
            player.exoPlayer.isCommandAvailable(Player.COMMAND_SET_VIDEO_SURFACE)
        }

        return if (reusablePlayer != null && videoPool.size == MAX_POOL_SIZE) {
            val oldUri = videoPool.entries.find { it.value == reusablePlayer }?.key
            oldUri?.let { videoPool.remove(it) }

            reusablePlayer.stop()
            val mediaSource = ProgressiveMediaSource.Factory(
                DefaultDataSource.Factory(context)
            ).createMediaSource(MediaItem.fromUri(uri))
            reusablePlayer.setMediaSource(mediaSource)
            reusablePlayer.prepare()
            reusablePlayer.exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
            videoPool[uri] = reusablePlayer
            reusablePlayer
        } else {
            if (videoPool.size >= MAX_POOL_SIZE) {
                // 보호되지 않은 가장 오래된 플레이어 찾아서 제거
                val oldestEntry = videoPool.keys.firstOrNull { !protectedUris.contains(it) }
                    ?: videoPool.keys.first() // 모두 보호 중이면 어쩔 수 없이 첫 번째 제거

                videoPool.remove(oldestEntry)?.release()
            }

            val newExoPlayer = ExoPlayer.Builder(context).build()
            val mediaSource = ProgressiveMediaSource.Factory(
                DefaultDataSource.Factory(context)
            ).createMediaSource(MediaItem.fromUri(uri))
            newExoPlayer.setMediaSource(mediaSource)
            newExoPlayer.prepare()
            newExoPlayer.repeatMode = Player.REPEAT_MODE_ONE

            VideoPlayer(newExoPlayer, uri).also {
                videoPool[uri] = it
            }
        }
    }

    fun playPlayer(uri: String) {
        lastPlayedUri = uri
        protectPlayer(uri) // 재생 시작하면 보호
        videoPool[uri]?.play()
    }

    fun pausePlayer(uri: String) {
        videoPool[uri]?.pause()
        unprotectPlayer(uri) // 일시정지하면 보호 해제
    }

    fun pauseAllPlayers() {
        videoPool.values.forEach { it.pause() }
        protectedUris.clear() // 전체 일시정지면 모든 보호 해제
    }

    fun resumeLastPlayed() {
        lastPlayedUri?.let { uri ->
            videoPool[uri]?.play()
            protectPlayer(uri)
        }
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
        protectedUris.clear()
    }
}
