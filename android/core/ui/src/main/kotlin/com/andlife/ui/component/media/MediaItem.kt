package com.andlife.ui.component.media

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.InvitationIconSize
import com.andlife.designsystem.theme.InvitationSpacing
import com.andlife.designsystem.theme.InvitationTheme
// import com.andlife.ui.model.MediaType
import com.andlife.domain.model.MediaType
import com.andlife.ui.R

@Composable
fun MediaItem(
    mediaUrl: String,
    mediaType: MediaType,
    modifier: Modifier = Modifier,
    duration: Int? = null,
    isEditMode: Boolean = false,
    onRemove: (() -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(InvitationSpacing.small))
            .clickable { onClick() }
    ) {
        when (mediaType) {
            MediaType.IMAGE -> {
                AsyncImage(
                    model = mediaUrl,
                    contentDescription = stringResource(R.string.desc_media_image),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            MediaType.AUDIO -> {
                Box(
                    modifier = Modifier.fillMaxSize().background(InvitationTheme.colorScheme.backgroundSecondary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_mic_24),
                        tint = InvitationTheme.colorScheme.brandPrimary,
                        modifier = Modifier.size(InvitationIconSize.xLarge),
                        contentDescription = stringResource(R.string.desc_media_audio),
                    )
                }
            }
            MediaType.VIDEO -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = mediaUrl,
                        contentDescription = stringResource(R.string.desc_media_video),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_play_circle_24),
                        contentDescription = stringResource(R.string.desc_ic_play),
                        modifier = Modifier.align(Alignment.Center).size(InvitationIconSize.xLarge),
                        tint = InvitationTheme.colorScheme.iconTertiary.copy(alpha = 0.8f)
                    )
                }
            }
        }

        if (duration != null && duration > 0) {
            DurationOverlay(
                duration = duration,
                modifier = Modifier.align(Alignment.BottomEnd).padding(InvitationSpacing.small)
            )
        }

        if (isEditMode) {
            IconButton(
                onClick = { onRemove?.invoke() },
                modifier = Modifier.align(Alignment.TopEnd).size(InvitationIconSize.medium).padding(InvitationSpacing.xSmall)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_cancel_24),
                    contentDescription = stringResource(R.string.desc_btn_remove),
                    tint = InvitationTheme.colorScheme.backgroundOverlay
                )
            }
        }
    }
}

@PreviewTheme
@Composable
fun EditModeVideoItemPreview() {
    InvitationTheme {
        MediaItem(
            mediaUrl = "https://picsum.photos/400/600?random=3",
            mediaType = MediaType.VIDEO,
            duration = 828,
            isEditMode = true,
            onRemove = {}
        )
    }
}

@PreviewTheme
@Composable
fun EditModeAudioItemPreview() {
    InvitationTheme {
        MediaItem(
            mediaUrl = "https://picsum.photos/400/600?random=3",
            mediaType = MediaType.AUDIO,
            duration = 314,
            isEditMode = true,
            onRemove = {}
        )
    }
}

