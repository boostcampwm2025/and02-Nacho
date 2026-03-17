package com.andlife.ui.component.media.video

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import coil3.compose.AsyncImage
import com.andlife.media.video.AutoVideoPlayer
import com.andlife.ui.component.guestbook.VideoSurface
import com.andlife.ui.component.media.video.FullscreenVideoContainerConstants.ANIM_FULLSCREEN_DURATION_MS
import com.andlife.ui.component.media.video.FullscreenVideoContainerConstants.ANIM_THUMBNAIL_FADE_DURATION_MS
import com.andlife.ui.util.findActivity
import com.andlife.ui.util.noRippleClickable
import com.andlife.ui.util.toFormatDuration
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private object FullscreenVideoContainerConstants {
    const val ANIM_FULLSCREEN_DURATION_MS = 300
    const val ANIM_THUMBNAIL_FADE_DURATION_MS = 200
}

@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(UnstableApi::class)
@Composable
fun FullscreenVideoContainer(
    player: AutoVideoPlayer,
    thumbnailUrl: String?,
    startBounds: Rect?,
    isMuted: Boolean,
    onDismiss: () -> Unit,
    onMuteToggle: () -> Unit,
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
    val fullBounds = Rect(0f, 0f, screenWidthPx, screenHeightPx)
    val initialBounds = startBounds ?: fullBounds

    var isExpanded by remember { mutableStateOf(false) }
    var isVideoReadyInFullscreen by remember { mutableStateOf(false) }
    var currentPositionMs by remember { mutableLongStateOf(0L) }
    var totalDurationMs by remember { mutableLongStateOf(0L) }
    var isSeeking by remember { mutableStateOf(false) }
    var seekPositionMs by remember { mutableLongStateOf(0L) }
    var isControlVisible by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }

    val displayPositionMs = if (isSeeking) seekPositionMs else currentPositionMs
    val progress = if (totalDurationMs > 0) {
        (displayPositionMs.toFloat() / totalDurationMs.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }
    val timeText = "${(displayPositionMs / 1000).toInt().toFormatDuration()} / ${
        (totalDurationMs / 1000).toInt().toFormatDuration()
    }"

    val animSpec: AnimationSpec<Float> = tween(durationMillis = ANIM_FULLSCREEN_DURATION_MS, easing = FastOutSlowInEasing)
    val animLeft by animateFloatAsState(
        targetValue = if (isExpanded) fullBounds.left else initialBounds.left,
        animationSpec = animSpec,
        label = "left"
    )
    val animTop by animateFloatAsState(
        targetValue = if (isExpanded) fullBounds.top else initialBounds.top,
        animationSpec = animSpec,
        label = "top"
    )
    val animWidth by animateFloatAsState(
        targetValue = if (isExpanded) fullBounds.width else initialBounds.width,
        animationSpec = animSpec,
        label = "width"
    )
    val animHeight by animateFloatAsState(
        targetValue = if (isExpanded) fullBounds.height else initialBounds.height,
        animationSpec = animSpec,
        label = "height"
    )
    val thumbnailAlpha by animateFloatAsState(
        targetValue = if (isVideoReadyInFullscreen) 0f else 1f,
        animationSpec = tween(durationMillis = ANIM_THUMBNAIL_FADE_DURATION_MS)
    )

    LaunchedEffect(Unit) {
        isExpanded = true
    }

    LaunchedEffect(isControlVisible) {
        if (isControlVisible) {
            delay(3000L)
            isControlVisible = false
        }
    }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }

            override fun onRenderedFirstFrame() {
                isVideoReadyInFullscreen = true
            }

            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY) {
                    isVideoReadyInFullscreen = true
                    totalDurationMs = player.exoPlayer.duration.coerceAtLeast(0L)
                }
            }
        }
        player.exoPlayer.addListener(listener)
        if (player.exoPlayer.playbackState == Player.STATE_READY) {
            isVideoReadyInFullscreen = true
            totalDurationMs = player.exoPlayer.duration.coerceAtLeast(0L)
        }
        isPlaying = player.exoPlayer.isPlaying
        onDispose { player.exoPlayer.removeListener(listener) }
    }

    LaunchedEffect(isVideoReadyInFullscreen) {
        if (!isVideoReadyInFullscreen) return@LaunchedEffect
        while (true) {
            if (!isSeeking) {
                currentPositionMs = player.exoPlayer.currentPosition.coerceAtLeast(0L)
            }
            delay(300L)
        }
    }

    val onPlayPauseClick: () -> Unit = {
        if (isPlaying) player.pause()
        else player.play()
    }

    val onOrientationClick: () -> Unit = {
        context.findActivity()?.requestedOrientation = if (isLandscape) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }

    val handleDismiss: () -> Unit = {
        scope.launch {
            context.findActivity()?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            isExpanded = false
            delay(250L)
            onDismiss()
        }
    }

    BackHandler { handleDismiss() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .noRippleClickable { isControlVisible = !isControlVisible },
    ) {
        Box(
            modifier = Modifier
                .then(
                    if (isLandscape) {
                        Modifier.fillMaxSize()
                    } else {
                        Modifier
                            .offset {
                                IntOffset(animLeft.roundToInt(), animTop.roundToInt())
                            }
                            .size(
                                width = with(density) { animWidth.toDp() },
                                height = with(density) { animHeight.toDp() },
                            )
                    }
                )
                .background(Color.Black)
        ) {
            VideoSurface(
                autoPlayer = player,
                modifier = Modifier.fillMaxSize(),
            )

            if (thumbnailUrl != null && thumbnailAlpha > 0f) {
                AsyncImage(
                    model = thumbnailUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(thumbnailAlpha),
                    contentScale = ContentScale.Fit,
                )
            }
        }
        FullscreenControlOverlay(
            progress = progress,
            timeText = timeText,
            isControlVisible = isControlVisible,
            isPlaying = isPlaying,
            isMuted = isMuted,
            isLandscape = isLandscape,
            onPlayPauseClick = onPlayPauseClick,
            onSeekValueChange = { newValue ->
                if (!isSeeking) player.pause()
                isSeeking = true
                seekPositionMs = (newValue * totalDurationMs).toLong()
            },
            onSeekValueChangeFinished = {
                player.seekTo(seekPositionMs)
                player.play()
                isSeeking = false
            },
            onMuteToggle = onMuteToggle,
            onOrientationClick = onOrientationClick,
            onExitFullscreen = { handleDismiss() },
        )
    }
}
