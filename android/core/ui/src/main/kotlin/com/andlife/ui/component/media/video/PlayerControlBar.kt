package com.andlife.ui.component.media.video

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
fun PlayerControlBar(
    progress: Float,
    timeText: String,
    isFullscreen: Boolean,
    onSeekValueChange: (Float) -> Unit,
    onSeekValueChangeFinished: () -> Unit,
    onFullscreenClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
//    val fullScreenIconRes = if (isFullscreen) {
//        R.drawable.ic_fullscreen_exit_24
//    } else {
//        R.drawable.ic_fullscreen_24
//    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                )
            )
            .padding(horizontal = NachoSpacing.small)
            .padding(bottom = NachoSpacing.medium),
//        verticalArrangement = Arrangement.spacedBy(NachoSpacing.medium),
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
                        .shadow(
                            elevation = NachoElevation.medium,
                            shape = CircleShape
                        )
                        .background(
                            color = Color.White,
                            shape = CircleShape,
                        )
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = timeText,
                style = NachoTheme.typography.bodySmallRegular,
                color = Color.White,
                modifier = Modifier.padding(start = NachoSpacing.xSmall)
            )
            PlayerControlBarIconButton(
                iconRes = R.drawable.ic_fullscreen_24,
                contentDescRes = R.string.desc_fullscreen,
                onClick = onFullscreenClick
            )
        }
    }
}
