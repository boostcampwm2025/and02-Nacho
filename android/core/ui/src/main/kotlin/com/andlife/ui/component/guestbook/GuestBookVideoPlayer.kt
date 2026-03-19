package com.andlife.ui.component.guestbook

import androidx.annotation.OptIn
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import coil3.compose.AsyncImage
import com.andlife.media.video.AutoVideoPlayer
import com.andlife.media.video.AutoVideoPlayerPool
import com.andlife.ui.component.media.video.CompactPlaybackControls
import com.andlife.ui.component.media.video.VideoSurface
import com.andlife.ui.component.media.video.VideoThumbnail
import com.andlife.ui.util.toFormatDuration
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class)
@Composable
fun GuestBookVideoPlayer(
    guestBookId: Long,
    videoUrl: String,
    thumbnailUrl: String?,
    totalDurationSeconds: Int?,
    shouldPlay: Boolean,
    isFullscreen: Boolean,
    videoPlayerPool: AutoVideoPlayerPool,
    onFullscreenClick: (String, String?, Rect) -> Unit,
    onPlayVideoClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isVideoReady by remember(videoUrl) { mutableStateOf(false) }
    var currentPositionMs by remember(videoUrl) { mutableLongStateOf(0L) }
    var totalDurationMs by remember(videoUrl) { mutableLongStateOf((totalDurationSeconds?.times(1000L)) ?: 0L) }
    var isControlVisible by remember(videoUrl) { mutableStateOf(false) }
    var isSeeking by remember(videoUrl) { mutableStateOf(false) }
    var seekPositionMs by remember(videoUrl) { mutableLongStateOf(0L) }
    val isMuted by videoPlayerPool.isMuted.collectAsStateWithLifecycle()
    var containerBounds by remember { mutableStateOf(Rect.Zero) }

    val displayPositionMs = if (isSeeking) seekPositionMs else currentPositionMs
    val progress = if (totalDurationMs > 0) {
        (displayPositionMs.toFloat() / totalDurationMs.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }
    val timeText = "${(displayPositionMs / 1000).toInt().toFormatDuration()} / ${
        (totalDurationMs / 1000).toInt().toFormatDuration()
    }"

    LaunchedEffect(shouldPlay, videoUrl, isFullscreen) {
        if (isFullscreen) return@LaunchedEffect

        if (shouldPlay) {
            videoPlayerPool.playPlayer(videoUrl, guestBookId)
        } else {
            videoPlayerPool.pausePlayer(videoUrl)
        }
    }

    LaunchedEffect(isControlVisible) {
        if (isControlVisible) {
            delay(3000L)
            isControlVisible = false
        }
    }

    LaunchedEffect(shouldPlay) {
        if (!shouldPlay) isControlVisible = false
    }

    if (shouldPlay) {
        val currentPlayer = remember(videoUrl) { videoPlayerPool.getPlayer(videoUrl) }

        DisposableEffect(currentPlayer, videoUrl) {
            val listener = object : Player.Listener {
                override fun onRenderedFirstFrame() {
                    isVideoReady = true
                }

                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_READY && currentPlayer.exoPlayer.playWhenReady) {
                        isVideoReady = true
                        totalDurationMs = currentPlayer.exoPlayer.duration.coerceAtLeast(0L)
                    }
                }
            }
            currentPlayer.exoPlayer.addListener(listener)
            if (currentPlayer.exoPlayer.playbackState == Player.STATE_READY) {
                isVideoReady = true
                totalDurationMs = currentPlayer.exoPlayer.duration.coerceAtLeast(0L)
            }
            onDispose {
                currentPlayer.exoPlayer.removeListener(listener)
            }
        }

        LaunchedEffect(isVideoReady) {
            if (!isVideoReady) {
                currentPositionMs = 0L
                return@LaunchedEffect
            }
            while (true) {
                if (!isSeeking) {
                    currentPositionMs = currentPlayer.exoPlayer.currentPosition.coerceAtLeast(0L)
                }
                delay(250L)
            }
        }

        VideoPlayerContainer(
            currentPlayer = currentPlayer,
            thumbnailUrl = thumbnailUrl,
            progress = progress,
            timeText = timeText,
            isControlVisible = isControlVisible,
            isMuted = isMuted,
            isVideoReady = isVideoReady,
            isFullscreen = isFullscreen,
            onVideoClick = { isControlVisible = !isControlVisible },
            onPlayVideoClick = { onPlayVideoClick(videoUrl) },
            onContainerPositioned = { containerBounds = it },
            onSeekValueChange = { newValue ->
                if (!isSeeking) currentPlayer.pause()
                isSeeking = true
                seekPositionMs = (newValue * totalDurationMs).toLong()
            },
            onSeekValueChangeFinished = {
                currentPlayer.seekTo(seekPositionMs)
                currentPlayer.play()
                isSeeking = false
            },
            onMuteToggle = { videoPlayerPool.toggleMute() },
            onFullscreenClick = { onFullscreenClick(videoUrl, thumbnailUrl, containerBounds) },
            modifier = modifier
        )
    } else {
        VideoPlayerContainer(
            currentPlayer = null,
            thumbnailUrl = thumbnailUrl,
            progress = 0f,
            timeText = "",
            isControlVisible = false,
            isMuted = isMuted,
            isVideoReady = false,
            isFullscreen = false,
            onVideoClick = {},
            onPlayVideoClick = { onPlayVideoClick(videoUrl) },
            onContainerPositioned = {},
            onSeekValueChange = {},
            onSeekValueChangeFinished = {},
            onMuteToggle = { videoPlayerPool.toggleMute() },
            onFullscreenClick = {},
            modifier = modifier
        )
    }
}

@Composable
private fun VideoPlayerContainer(
    currentPlayer: AutoVideoPlayer?,
    thumbnailUrl: String?,
    progress: Float,
    timeText: String,
    isControlVisible: Boolean,
    isMuted: Boolean,
    isVideoReady: Boolean,
    isFullscreen: Boolean,
    onVideoClick: () -> Unit,
    onPlayVideoClick: () -> Unit,
    onContainerPositioned: (Rect) -> Unit,
    onSeekValueChange: (Float) -> Unit,
    onSeekValueChangeFinished: () -> Unit,
    onMuteToggle: () -> Unit,
    onFullscreenClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val thumbnailAlpha by animateFloatAsState(
        targetValue = if (isVideoReady) 0f else 1f,
        animationSpec = tween(durationMillis = 200),
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .onGloballyPositioned { coordinates ->
                onContainerPositioned(coordinates.boundsInWindow(clipBounds = false))
            }
            .then(
                if (isVideoReady) Modifier.clickable { onVideoClick() }
                else Modifier
            )
    ) {
        currentPlayer?.let {
            if (!isFullscreen) {
                VideoSurface(
                    autoPlayer = it,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                if (thumbnailUrl != null) {
                    AsyncImage(
                        model = thumbnailUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                    )
                }
            }
        }

        if (thumbnailUrl != null && thumbnailAlpha > 0f) {
            VideoThumbnail(
                thumbnailUrl = thumbnailUrl,
                onPlayVideoClick = onPlayVideoClick,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(thumbnailAlpha),
            )
        }

        if (currentPlayer != null) {
            CompactPlaybackControls(
                progress = progress,
                timeText = timeText,
                isControlVisible = isControlVisible,
                isMuted = isMuted,
                onSeekValueChange = onSeekValueChange,
                onSeekValueChangeFinished = onSeekValueChangeFinished,
                onMuteToggle = onMuteToggle,
                onFullscreenClick = onFullscreenClick,
            )
        }
    }
}
