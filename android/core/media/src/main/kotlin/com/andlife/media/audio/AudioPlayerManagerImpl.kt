package com.andlife.media.audio

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AudioInfo(
    val url: String,
    val totalDurationMs: Long,
    val currentPositionMs: Long,
    val isPlaying: Boolean
)

class AudioPlayerManagerImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : AudioPlayerManager {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var exoPlayer: ExoPlayer? = null
    private var timerJob: Job? = null

    private val _currentAudio = MutableStateFlow<AudioInfo?>(null)
    override val currentTrack = _currentAudio.asStateFlow()

    private fun preparePlayer() {
        if (exoPlayer != null) return

        exoPlayer = ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_OFF
            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _currentAudio.update { it?.copy(isPlaying = isPlaying) }

                    if (isPlaying) {
                        controlTimer()
                    } else {
                        timerJob?.cancel()
                    }
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_READY -> {
                            _currentAudio.update {
                                it?.copy(
                                    totalDurationMs = this@apply.duration
                                )
                            }
                        }

                        Player.STATE_ENDED -> {
                            timerJob?.cancel()
                            _currentAudio.update { null }
                        }

                        Player.STATE_BUFFERING -> {
                            // No-op
                        }

                        Player.STATE_IDLE -> {
                            // No-op
                        }
                    }
                }
            })
        }
    }

    override fun togglePlay(url: String) {
        preparePlayer()
        val player = exoPlayer ?: return

        val current = _currentAudio.value

        if (current?.url == url) {
            if (player.isPlaying) {
                player.pause()
            } else {
                player.play()
            }
        } else {
            timerJob?.cancel()
            player.stop()
            player.clearMediaItems()

            _currentAudio.update {
                AudioInfo(
                    url = url,
                    totalDurationMs = 0L,
                    currentPositionMs = 0L,
                    isPlaying = false
                )
            }

            val mediaItem = MediaItem.fromUri(url)
            val mediaSource =
                ProgressiveMediaSource.Factory(DefaultDataSource.Factory(context))
                    .createMediaSource(mediaItem)

            player.setMediaSource(mediaSource)
            player.prepare()
            player.play()
        }
    }

    private fun controlTimer() {
        timerJob?.cancel()
        val player = exoPlayer ?: return

        timerJob = scope.launch {
            while (isActive && player.isPlaying) {
                _currentAudio.update {
                    it?.copy(
                        currentPositionMs = player.currentPosition
                    )
                }
                delay(1000L)
            }
        }
    }

    override fun pause() {
        exoPlayer?.pause()
    }

    override fun stopAll() {
        timerJob?.cancel()
        exoPlayer?.stop()
        _currentAudio.update { null }
    }

    override fun release() {
        timerJob?.cancel()
        scope.cancel()
        exoPlayer?.release()
        exoPlayer = null
        _currentAudio.update { null }
    }
}
