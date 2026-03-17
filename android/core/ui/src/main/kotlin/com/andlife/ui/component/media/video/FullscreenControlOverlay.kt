package com.andlife.ui.component.media.video

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.andlife.designsystem.theme.NachoElevation
import com.andlife.designsystem.theme.NachoIconSize
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoStroke
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.ui.R
import com.andlife.ui.component.media.video.FullscreenControlOverlayConstants.ANIM_FULLSCREEN_DURATION
import com.andlife.ui.util.noRippleClickable

private object FullscreenControlOverlayConstants {
    const val ANIM_FULLSCREEN_DURATION = 300
}

@Composable
fun FullscreenControlOverlay(
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
            FullscreenControlBar(
                progress = progress,
                timeText = timeText,
                playPauseIconRes = playPauseIconRes,
                playPauseIconDesc = playPauseIconDesc,
                isMuted = isMuted,
                isLandscape = isLandscape,
                onPlayPauseClick = onPlayPauseClick,
                onSeekValueChange = onSeekValueChange,
                onSeekValueChangeFinished = onSeekValueChangeFinished,
                onMuteToggle = onMuteToggle,
                onOrientationClick = onOrientationClick,
                onExitFullscreen = onExitFullscreen,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FullscreenControlBar(
    progress: Float,
    timeText: String,
    @DrawableRes playPauseIconRes: Int,
    @StringRes playPauseIconDesc: Int,
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
    val (muteIconRes, muteIconDesc) = if (isMuted) {
        R.drawable.ic_volume_off_filled_24 to R.string.desc_unmute_video
    } else {
        R.drawable.ic_volume_up_filled_24 to R.string.desc_mute_video
    }
    val orientationIconDesc = if (isLandscape) R.string.desc_portrait else R.string.desc_landscape

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                )
            )
            .padding(horizontal = NachoSpacing.small)
            .padding(bottom = NachoSpacing.xLarge),
    ) {
        Slider(
            value = progress,
            onValueChange = onSeekValueChange,
            onValueChangeFinished = onSeekValueChangeFinished,
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp),
            thumb = {
                Box(
                    modifier = Modifier
                        .size(NachoIconSize.xSmall)
                        .shadow(elevation = NachoElevation.medium, shape = CircleShape)
                        .background(color = Color.White, shape = CircleShape)
                )
            },
            track = { sliderState ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(NachoStroke.large)
                        .clip(NachoTheme.shapes.extraSmall)
                        .background(NachoTheme.colorScheme.backgroundBorder)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(sliderState.value)
                            .fillMaxHeight()
                            .background(NachoTheme.colorScheme.brandPrimary)
                    )
                }
            },
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = NachoSpacing.xSmall, vertical = NachoSpacing.xSmall),
            horizontalArrangement = Arrangement.spacedBy(NachoSpacing.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PlayerControlBarIconButton(
                iconRes = playPauseIconRes,
                contentDescRes = playPauseIconDesc,
                onClick = onPlayPauseClick,
            )
            Text(
                text = timeText,
                style = NachoTheme.typography.bodySmallRegular,
                color = Color.White,
            )
            PlayerControlBarIconButton(
                iconRes = muteIconRes,
                contentDescRes = muteIconDesc,
                onClick = onMuteToggle,
            )
            Spacer(modifier = Modifier.weight(1f))
            PlayerControlBarIconButton(
                iconRes = R.drawable.ic_screen_rotation_24,
                contentDescRes = orientationIconDesc,
                onClick = onOrientationClick,
            )
            PlayerControlBarIconButton(
                iconRes = R.drawable.ic_fullscreen_exit_24,
                contentDescRes = R.string.desc_exit_fullscreen,
                onClick = onExitFullscreen,
            )
        }
    }
}

@Composable
fun PlayerControlBarIconButton(
    @DrawableRes iconRes: Int,
    @StringRes contentDescRes: Int,
    color: Color = Color.White,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(
                color = Color.Transparent,
                shape = CircleShape,
            )
            .noRippleClickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = stringResource(contentDescRes),
            tint = color,
            modifier = Modifier.padding(NachoSpacing.xSmall)
        )
    }
}
