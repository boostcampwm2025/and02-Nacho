package com.andlife.ui.component.media.video

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.andlife.designsystem.theme.NachoIconSize
import com.andlife.ui.R
import com.andlife.ui.component.media.video.FullscreenControlOverlayConstants.ANIM_FULLSCREEN_DURATION
import com.andlife.ui.util.noRippleClickable

private object FullscreenControlOverlayConstants {
    const val ANIM_FULLSCREEN_DURATION = 300
}

@Composable
fun FullscreenPlaybackControls(
    progress: Float,
    timeText: String,
    isControlVisible: Boolean,
    isPlaying: Boolean,
    isMuted: Boolean,
    isLandscape: Boolean,
    onPlayPauseClick: () -> Unit,
    onSeekValueChange: (Float) -> Unit,
    onSeekValueChangeFinished: () -> Unit,
    onMuteToggle: () -> Unit,
    onOrientationClick: () -> Unit,
    onExitFullscreen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (playPauseIconRes, playPauseIconDesc) = if (isPlaying) {
        R.drawable.ic_pause_filled_24 to R.string.desc_pause_video
    } else {
        R.drawable.ic_play_arrow_24 to R.string.desc_play_video
    }

    Box(modifier = modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = isControlVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center),
        ) {
            Box(
                modifier = Modifier
                    .size(NachoIconSize.twoXLarge)
                    .background(
                        color = Color.Black.copy(alpha = 0.4f),
                        shape = CircleShape,
                    )
                    .noRippleClickable { onPlayPauseClick() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(playPauseIconRes),
                    contentDescription = stringResource(playPauseIconDesc),
                    tint = Color.White,
                    modifier = Modifier.size(NachoIconSize.large)
                )
            }
        }
        AnimatedVisibility(
            visible = isControlVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(durationMillis = ANIM_FULLSCREEN_DURATION),
            ) + fadeIn(animationSpec = tween(durationMillis = ANIM_FULLSCREEN_DURATION)),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(durationMillis = ANIM_FULLSCREEN_DURATION),
            ) + fadeOut(animationSpec = tween(durationMillis = ANIM_FULLSCREEN_DURATION)),
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            VideoPlayerControlBar(
                progress = progress,
                timeText = timeText,
                isFullscreen = true,
                onSeekValueChange = onSeekValueChange,
                onSeekValueChangeFinished = onSeekValueChangeFinished,
                onFullscreenClick = onExitFullscreen,
                playPauseIconRes = playPauseIconRes,
                playPauseIconDesc = playPauseIconDesc,
                isMuted = isMuted,
                isLandscape = isLandscape,
                onPlayPauseClick = onPlayPauseClick,
                onMuteToggle = onMuteToggle,
                onOrientationClick = onOrientationClick,
            )
        }
    }
}
