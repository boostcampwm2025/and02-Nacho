package com.andlife.ui.component.media.video

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
import com.andlife.ui.util.noRippleClickable

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
    Box(modifier = modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = isControlVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center),
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.5f),
                        shape = CircleShape,
                    )
                    .noRippleClickable { onPlayPauseClick() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(
                        if (isPlaying) R.drawable.ic_pause_filled_24
                        else R.drawable.ic_play_arrow_24
                    ),
                    contentDescription = if (isPlaying) "일시정지" else "재생",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp),
                )
            }
        }

        AnimatedVisibility(
            visible = isControlVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(durationMillis = 250),
            ) + fadeIn(animationSpec = tween(durationMillis = 250)),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(durationMillis = 250),
            ) + fadeOut(animationSpec = tween(durationMillis = 250)),
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            FullscreenControlBar(
                progress = progress,
                timeText = timeText,
                isPlaying = isPlaying,
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

@kotlin.OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FullscreenControlBar(
    progress: Float,
    timeText: String,
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
            Icon(
                painter = painterResource(
                    if (isPlaying) R.drawable.ic_pause_filled_24
                    else R.drawable.ic_play_arrow_24
                ),
                contentDescription = if (isPlaying) "일시정지" else "재생",
                tint = Color.White,
                modifier = Modifier.noRippleClickable { onPlayPauseClick() },
            )
            Text(
                text = timeText,
                style = NachoTheme.typography.bodySmallRegular,
                color = Color.White,
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(
                    if (isMuted) R.drawable.ic_volume_off_filled_24
                    else R.drawable.ic_volume_up_filled_24
                ),
                contentDescription = if (isMuted) "음소거 해제" else "음소거",
                tint = Color.White,
                modifier = Modifier.noRippleClickable { onMuteToggle() },
            )
            Icon(
                painter = painterResource(R.drawable.ic_screen_rotation_24),
                contentDescription = if (isLandscape) "세로 모드" else "가로 모드",
                tint = Color.White,
                modifier = Modifier.noRippleClickable { onOrientationClick() },
            )
            Icon(
                painter = painterResource(R.drawable.ic_fullscreen_exit_24),
                contentDescription = stringResource(R.string.desc_exit_fullscreen),
                tint = Color.White,
                modifier = Modifier.noRippleClickable { onExitFullscreen() },
            )
        }
    }
}

