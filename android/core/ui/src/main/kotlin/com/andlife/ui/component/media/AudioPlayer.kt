package com.andlife.ui.component.media

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.media3.common.Player
import com.andlife.designsystem.theme.NachoIconSize
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.ui.R
import com.andlife.ui.util.toDurationFormat
import kotlinx.coroutines.delay

@Composable
fun AudioPlayer(
    exoPlayer: Player,
    modifier: Modifier = Modifier
) {
    var isPlaying by remember { mutableStateOf(exoPlayer.isPlaying) }
    var currentPosition by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(exoPlayer.duration.coerceAtLeast(0L)) }
    var isDragging by remember { mutableStateOf(false) }
    var sliderPosition by remember { mutableFloatStateOf(0f) }

    // ExoPlayer 이벤트 리스너 (재생/일시정지 상태 감지)
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlayingState: Boolean) {
                isPlaying = isPlayingState
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    duration = exoPlayer.duration.coerceAtLeast(0L)
                }
            }
        }
        exoPlayer.addListener(listener)
        onDispose { exoPlayer.removeListener(listener) }
    }

    // 재생 바 업데이트를 위한 루프
    LaunchedEffect(isPlaying, isDragging) {
        if (!isDragging) {
            while (isPlaying) {
                currentPosition = exoPlayer.currentPosition
                delay(100)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NachoTheme.colorScheme.backgroundPrimary)
            .background(NachoTheme.colorScheme.backgroundOverlay)
            .padding(horizontal = NachoSpacing.twoXLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(NachoIconSize.huge)
                .clip(CircleShape)
                .background(NachoTheme.colorScheme.iconPrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_mic_24),
                contentDescription = stringResource(R.string.desc_ic_mic),
                tint = NachoTheme.colorScheme.iconTertiary,
                modifier = Modifier.size(NachoIconSize.xLarge)
            )
        }

        Spacer(modifier = Modifier.height(NachoSpacing.threeXLarge))

        Text(
            text = currentPosition.toDurationFormat(),
            style = NachoTheme.typography.headingMedium.copy(
                color = NachoTheme.colorScheme.textOnPrimary
            )
        )

        Spacer(modifier = Modifier.height(NachoSpacing.twoXLarge))

        Box(
            modifier = Modifier
                .size(NachoIconSize.twoXLarge)
                .clip(CircleShape)
                .background(NachoTheme.colorScheme.iconPrimary)
                .clickable {
                    if (exoPlayer.isPlaying) {
                        exoPlayer.pause()
                    } else {
                        exoPlayer.play()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(
                    id = if (isPlaying) R.drawable.ic_pause_24 else R.drawable.ic_play_arrow_24
                ),
                contentDescription = stringResource(R.string.desc_btn_play),
                tint = NachoTheme.colorScheme.iconTertiary,
                modifier = Modifier.size(NachoIconSize.large)
            )
        }

        Spacer(modifier = Modifier.height(NachoSpacing.threeXLarge))

        val progress = if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f

        Slider(
            value = if (isDragging) sliderPosition else {
                if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f
            },
            onValueChange = {
                isDragging = true
                sliderPosition = it
            },
            onValueChangeFinished = {
                val seekTo = (sliderPosition * duration).toLong()
                exoPlayer.seekTo(seekTo)
                isDragging = false
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = NachoSpacing.medium),
            colors = SliderDefaults.colors(
                thumbColor = NachoTheme.colorScheme.iconPrimary,
                activeTrackColor = NachoTheme.colorScheme.iconPrimary,
                inactiveTrackColor = NachoTheme.colorScheme.backgroundPrimary.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
fun AudioProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(NachoTheme.colorScheme.backgroundPrimary.copy(alpha = 0.5f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction = progress)
                .clip(RoundedCornerShape(50))
                .background(NachoTheme.colorScheme.backgroundPrimary)
        )
    }
}
