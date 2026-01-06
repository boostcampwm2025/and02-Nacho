package com.andlife.ui.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource

object VideoPlayerPool {
    private const val MAX_POOL_SIZE = 5 // 최대 플레이어 수, 일단 5개로 설정, 필요시 조정 가능
    private val videoPool = LinkedHashMap<String, VideoPlayer>(MAX_POOL_SIZE, 0.75f)

    fun getPlayer(context: Context, uri: String): VideoPlayer {
        // 이미 해당 uri에 대한 플레이어가 존재하면 반환
        videoPool[uri]?.let { return it }

        val reusablePlayer = videoPool.values.find { player ->
            player.exoPlayer.isCommandAvailable(Player.COMMAND_SET_VIDEO_SURFACE) // ExoPlayer의 isCommandAvailable 메서드를 사용하여 현재 비디오 Surface를 설정할 수 있는지 확인
        }

        return if (reusablePlayer != null && videoPool.size == MAX_POOL_SIZE) { // 재사용 가능한 플레이어가 있고, 최대 크기에 도달한 경우
            // 재사용
            val oldUri = videoPool.entries.find { it.value == reusablePlayer }?.key // 기존 URI 찾기
            oldUri?.let { videoPool.remove(it) } // 기존 플레이어 제거

            reusablePlayer.stop() // 플레이어 정지
            val mediaSource = ProgressiveMediaSource.Factory(
                DefaultDataSource.Factory(context)
            ).createMediaSource(MediaItem.fromUri(uri))
            reusablePlayer.setMediaSource(mediaSource)
            reusablePlayer.prepare()
            videoPool[uri] = reusablePlayer
            reusablePlayer
        } else {
            // 풀이 가득 찼으면 가장 오래된 플레이어 제거
            if (videoPool.size >= MAX_POOL_SIZE) {
                val oldestEntry = videoPool.keys.first() // 가장 오래된 항목의 키 -> 첫 번째 키
                videoPool.remove(oldestEntry)?.release()
            }

            // 새 플레이어 생성
            val newExoPlayer = ExoPlayer.Builder(context).build()
            val mediaSource = ProgressiveMediaSource.Factory(
                DefaultDataSource.Factory(context)
            ).createMediaSource(MediaItem.fromUri(uri))
            newExoPlayer.setMediaSource(mediaSource)
            newExoPlayer.prepare()
            newExoPlayer.repeatMode = Player.REPEAT_MODE_ONE // 반복 재생 설정 -> 나중에 필요시 변경 가능, 테스트 해보자.

            VideoPlayer(newExoPlayer, uri).also {
                videoPool[uri] = it
            }
        }
    }

    fun playPlayer(uri: String) {
        videoPool[uri]?.play()
    }

    fun pausePlayer(uri: String) {
        videoPool[uri]?.pause()
    }

    fun releaseAll() {
        videoPool.values.forEach { it.release() }
        videoPool.clear()
    }
}
