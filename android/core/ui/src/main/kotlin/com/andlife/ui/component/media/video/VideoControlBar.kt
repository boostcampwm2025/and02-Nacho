package com.andlife.ui.component.media.video

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.andlife.designsystem.theme.NachoElevation
import com.andlife.designsystem.theme.NachoIconSize
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoStroke
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoControlBar(
    progress: Float,
    timeText: String,
    isFullscreen: Boolean,
    onSeekValueChange: (Float) -> Unit,
    onSeekValueChangeFinished: () -> Unit,
    onFullscreenClick: () -> Unit, // 리스트 -> 전체화면 혹은 전체화면 -> 리스트(종료)
    modifier: Modifier = Modifier,
    @DrawableRes playPauseIconRes: Int = R.drawable.ic_play_arrow_24,
    @StringRes playPauseIconDesc: Int = R.string.desc_play_video,
    isMuted: Boolean = false,
    isLandscape: Boolean = false,
    onPlayPauseClick: () -> Unit = {},
    onMuteToggle: () -> Unit = {},
    onOrientationClick: () -> Unit = {},
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
            .padding(bottom = if (isFullscreen) NachoSpacing.xLarge else NachoSpacing.medium),
    ) {
        VideoSlider(
            progress = progress,
            onSeekValueChange = onSeekValueChange,
            onSeekValueChangeFinished = onSeekValueChangeFinished
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = NachoSpacing.xSmall, vertical = NachoSpacing.xSmall),
            horizontalArrangement = if (isFullscreen) Arrangement.spacedBy(NachoSpacing.small) else Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isFullscreen) {
                PlayerControlBarIconButton(
                    iconRes = playPauseIconRes,
                    contentDescRes = playPauseIconDesc,
                    onClick = onPlayPauseClick,
                )
            }
            Text(
                text = timeText,
                style = NachoTheme.typography.bodySmallRegular,
                color = Color.White,
                modifier = if (!isFullscreen) Modifier.padding(start = NachoSpacing.xSmall) else Modifier
            )
            if (isFullscreen) {
                PlayerControlBarIconButton(
                    iconRes = muteIconRes,
                    contentDescRes = muteIconDesc,
                    onClick = onMuteToggle
                )
            }
            if (isFullscreen) Spacer(modifier = Modifier.weight(1f))
            if (isFullscreen) {
                PlayerControlBarIconButton(
                    iconRes = R.drawable.ic_screen_rotation_24,
                    contentDescRes = orientationIconDesc,
                    onClick = onOrientationClick
                )
                PlayerControlBarIconButton(
                    iconRes = R.drawable.ic_fullscreen_exit_24,
                    contentDescRes = R.string.desc_exit_fullscreen,
                    onClick = onFullscreenClick
                )
            } else {
                PlayerControlBarIconButton(
                    iconRes = R.drawable.ic_fullscreen_24,
                    contentDescRes = R.string.desc_fullscreen,
                    onClick = onFullscreenClick
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VideoSlider(
    progress: Float,
    onSeekValueChange: (Float) -> Unit,
    onSeekValueChangeFinished: () -> Unit
) {
    Slider(
        value = progress,
        onValueChange = onSeekValueChange,
        onValueChangeFinished = onSeekValueChangeFinished,
        modifier = Modifier.fillMaxWidth().height(24.dp),
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
        }
    )
}
