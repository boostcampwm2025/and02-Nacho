@file:Suppress("COMPOSE_APPLIER_CALL_MISMATCH")

package com.andlife.ui.component.media

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoStroke
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.ui.util.toDurationFormat
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    exoPlayer: Player,
    modifier: Modifier = Modifier
) {
    var currentPosition by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(exoPlayer.duration.coerceAtLeast(1L)) }

    var isDragging by remember { mutableStateOf(false) }
    var sliderPosition by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(exoPlayer, isDragging) {
        if (!isDragging) {
            while (true) {
                currentPosition = exoPlayer.currentPosition
                duration = exoPlayer.duration.coerceAtLeast(1L)
                delay(100)
            }
        }
    }

    Box(modifier = modifier) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                }
            },
            update = { playerView ->
                if (playerView.player != exoPlayer) {
                    playerView.player = exoPlayer
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // 프로그레스 바 영역
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(NachoSpacing.twoXLarge) // 터치 영역 확보
                .align(Alignment.BottomCenter),
            contentAlignment = Alignment.BottomCenter
        ) {
            val maxWidth = maxWidth
            val progress = if (isDragging) sliderPosition else {
                if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isDragging) NachoStroke.large else NachoStroke.medium)
                    .background(NachoTheme.colorScheme.textTertiary.copy(alpha = 0.3f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .background(NachoTheme.colorScheme.brandPrimary)
                )
            }

            if (isDragging) {
                val draggingTime = (progress * duration).toLong()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .offset(y = -NachoSpacing.large)
                ) {
                    Box(
                        modifier = Modifier
                            .offset(x = (maxWidth * progress))
                            .layout { measurable, constraints ->
                                val placeable = measurable.measure(constraints)
                                layout(placeable.width, placeable.height) {
                                    placeable.placeRelative(-(placeable.width / 2), 0)
                                }
                            }
                            .background(
                                color = NachoTheme.colorScheme.iconSecondary.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(NachoSpacing.xSmall)
                            )
                            .padding(horizontal = NachoSpacing.small, vertical = NachoSpacing.xSmall)
                    ) {
                        Text(
                            text = draggingTime.toDurationFormat(),
                            style = NachoTheme.typography.bodySmallSemiBold,
                            color = NachoTheme.colorScheme.textOnPrimary
                        )
                    }
                }
            }

            Slider(
                value = progress,
                onValueChange = {
                    isDragging = true
                    sliderPosition = it
                },
                onValueChangeFinished = {
                    val seekTo = (sliderPosition * duration).toLong()
                    exoPlayer.seekTo(seekTo)
                    isDragging = false
                },
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Color.Transparent,
                    activeTrackColor = Color.Transparent,
                    inactiveTrackColor = Color.Transparent,
                    disabledThumbColor = Color.Transparent,
                    disabledActiveTrackColor = Color.Transparent,
                    disabledInactiveTrackColor = Color.Transparent
                )
            )
        }
    }
}
