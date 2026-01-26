package com.andlife.media.audio

import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.andlife.media.di.ApplicationMainScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
    @param:ApplicationMainScope private val applicationScope: CoroutineScope
) : AudioPlayerManager {

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

    @OptIn(UnstableApi::class)
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

        timerJob = applicationScope.launch {
            do {
                _currentAudio.update {
                    it?.copy(
                        currentPositionMs = exoPlayer?.currentPosition ?: 0L
                    )
                }
                delay(100L)
            } while (_currentAudio.value?.isPlaying == true && exoPlayer?.isPlaying == true)
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
        Log.d("AudioPlayerManager", "Releasing AudioPlayerManager resources")
        timerJob?.cancel()
        timerJob = null
        exoPlayer?.release()
        exoPlayer = null
        _currentAudio.update { null }
    }
}
