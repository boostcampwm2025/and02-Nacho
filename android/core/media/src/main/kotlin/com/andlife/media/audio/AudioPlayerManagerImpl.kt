package com.andlife.media.audio

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class AudioPlayerManagerImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : AudioPlayerManager {

    private var exoPlayer: ExoPlayer? = null

    private val _currentAudioUrl = MutableStateFlow<String?>(null)
    override val currentAudioUrl = _currentAudioUrl.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying = _isPlaying.asStateFlow()

    private fun preparePlayer() {
        if (exoPlayer != null) return

        exoPlayer = ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_OFF
            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.update { isPlaying }
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED) {
                        _isPlaying.update { false }
                        _currentAudioUrl.update { null }
                    }
                }
            })
        }
    }

    override fun togglePlay(url: String) {
        preparePlayer()
        val player = exoPlayer ?: return

        if (_currentAudioUrl.value == url) {
            if (player.isPlaying) {
                player.pause()
            } else {
                player.play()
            }
        } else {
            player.stop()
            player.clearMediaItems()

            _currentAudioUrl.update { url }

            val mediaItem = MediaItem.fromUri(url)
            val mediaSource =
                ProgressiveMediaSource.Factory(DefaultDataSource.Factory(context))
                    .createMediaSource(mediaItem)

            player.setMediaSource(mediaSource)
            player.prepare()
            player.play()
        }
    }

    override fun pause() {
        exoPlayer?.pause()
    }

    override fun stopAll() {
        exoPlayer?.stop()
        _currentAudioUrl.update { null }
        _isPlaying.update { false }
    }

    override fun release() {
        exoPlayer?.release()
        exoPlayer = null
        _currentAudioUrl.update { null }
        _isPlaying.update { false }
    }
}
