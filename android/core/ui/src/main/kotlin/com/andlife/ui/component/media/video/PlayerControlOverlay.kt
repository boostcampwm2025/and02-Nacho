package com.andlife.ui.component.media.video

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoStroke
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.ui.R
import com.andlife.ui.component.media.video.PlayerControlOverlayConstants.ANIM_CONTROL_BAR_DURATION

private object PlayerControlOverlayConstants {
    const val ANIM_CONTROL_BAR_DURATION = 250
}

@Composable
fun PlayerControlOverlay(
    progress: Float,
    timeText: String,
    isControlVisible: Boolean,
    isMuted: Boolean,
    isFullscreen: Boolean,
    onSeekValueChange: (Float) -> Unit,
    onSeekValueChangeFinished: () -> Unit,
    onMuteToggle: () -> Unit,
    onFullscreenClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = !isControlVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            PlayerSeekbar(
                progress = progress,
                modifier = Modifier.fillMaxWidth()
            )
        }

        AnimatedVisibility(
            visible = isControlVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(durationMillis = ANIM_CONTROL_BAR_DURATION)
            ) + fadeIn(animationSpec = tween(durationMillis = ANIM_CONTROL_BAR_DURATION)),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(durationMillis = ANIM_CONTROL_BAR_DURATION)
            ) + fadeOut(animationSpec = tween(durationMillis = ANIM_CONTROL_BAR_DURATION)),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            PlayerControlBar(
                progress = progress,
                timeText = timeText,
                isFullscreen = isFullscreen,
                onSeekValueChange = onSeekValueChange,
                onSeekValueChangeFinished = onSeekValueChangeFinished,
                onFullscreenClick = onFullscreenClick,
            )
        }

        PlayerMuteButton(
            isMuted = isMuted,
            onToggle = onMuteToggle,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(NachoSpacing.small),
        )
    }
}

@Composable
private fun PlayerSeekbar(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    LinearProgressIndicator(
        progress = { progress },
        modifier = modifier
            .fillMaxWidth()
            .height(NachoStroke.medium),
        color = NachoTheme.colorScheme.brandPrimary,
        trackColor = NachoTheme.colorScheme.backgroundBorder,
        gapSize = NachoSpacing.none,
        strokeCap = StrokeCap.Square,
        drawStopIndicator = { /* No-op */ },
    )
}

@Composable
private fun PlayerMuteButton(
    isMuted: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val iconResId = if (isMuted) {
        R.drawable.ic_volume_off_filled_24
    } else {
        R.drawable.ic_volume_up_filled_24
    }
    val contentDescription = if (isMuted) {
        stringResource(R.string.desc_unmute_video)
    } else {
        stringResource(R.string.desc_mute_video)
    }
    IconButton(onClick = onToggle,) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = contentDescription,
            tint = NachoTheme.colorScheme.iconTertiary,
        )
    }
}
