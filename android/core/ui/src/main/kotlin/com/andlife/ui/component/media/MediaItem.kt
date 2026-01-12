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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.InvitationIconSize
import com.andlife.designsystem.theme.InvitationSpacing
import com.andlife.designsystem.theme.InvitationTheme
import com.andlife.ui.R
import com.andlife.ui.model.UiMediaType
import com.andlife.ui.util.toFormatDuration

@Composable
fun MediaItem(
    mediaUrl: String,
    mediaType: UiMediaType,
    modifier: Modifier = Modifier,
    thumbnailUrl: String? = null,
    duration: Int? = null,
    isEditMode: Boolean = false,
    onRemove: (() -> Unit)? = null,
    onClick: () -> Unit = {},
) {
    Box(
        modifier =
            modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(InvitationSpacing.small))
                .clickable { onClick() },
    ) {
        when (mediaType) {
            UiMediaType.IMAGE -> {
                SubcomposeAsyncImage(
                    ImageRequest
                        .Builder(LocalContext.current)
                        .data(mediaUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = stringResource(R.string.desc_media_image),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    success = {
                        SubcomposeAsyncImageContent()
                    },
                    loading = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_image_24),
                            contentDescription = null,
                            tint = InvitationTheme.colorScheme.iconDisabled
                        )
                    },
                    error = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_error_image_24),
                            contentDescription = null,
                            tint = InvitationTheme.colorScheme.iconDisabled
                        )
                    }
                )
            }
            UiMediaType.AUDIO -> {
                Box(
                    modifier = Modifier.fillMaxSize().background(InvitationTheme.colorScheme.backgroundSecondary),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_mic_24),
                        tint = InvitationTheme.colorScheme.brandPrimary,
                        modifier = Modifier.size(InvitationIconSize.xLarge),
                        contentDescription = stringResource(R.string.desc_media_audio),
                    )
                }
            }
            UiMediaType.VIDEO -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    SubcomposeAsyncImage(
                        ImageRequest
                            .Builder(LocalContext.current)
                            .data(thumbnailUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = stringResource(R.string.desc_media_video),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        success = {
                            SubcomposeAsyncImageContent()
                        },
                        loading = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_play_circle_24),
                                contentDescription = null,
                                tint = InvitationTheme.colorScheme.iconDisabled
                            )
                        },
                        error = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_error_image_24),
                                contentDescription = null,
                                tint = InvitationTheme.colorScheme.iconDisabled
                            )
                        }
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_play_circle_24),
                        contentDescription = stringResource(R.string.desc_ic_play),
                        modifier = Modifier.align(Alignment.Center).size(InvitationIconSize.xLarge),
                        tint = InvitationTheme.colorScheme.iconTertiary.copy(alpha = 0.8f),
                    )
                }
            }
        }

        if (duration != null && duration > 0) {
            MediaOverlay(
                text = duration.toFormatDuration(),
                modifier = Modifier.align(Alignment.BottomEnd).padding(InvitationSpacing.small),
            )
        }

        if (isEditMode) {
            IconButton(
                onClick = { onRemove?.invoke() },
                modifier =
                    Modifier
                        .align(
                            Alignment.TopEnd,
                        ).size(InvitationIconSize.medium)
                        .padding(InvitationSpacing.xSmall),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_cancel_24),
                    contentDescription = stringResource(R.string.desc_btn_remove),
                    tint = InvitationTheme.colorScheme.backgroundOverlay,
                )
            }
        }
    }
}

@PreviewTheme
@Composable
private fun EditModeVideoItemPreview() {
    InvitationTheme {
        MediaItem(
            mediaUrl = "https://picsum.photos/400/600?random=3",
            mediaType = UiMediaType.VIDEO,
            duration = 828,
            isEditMode = true,
            onRemove = {},
        )
    }
}

@PreviewTheme
@Composable
private fun EditModeAudioItemPreview() {
    InvitationTheme {
        MediaItem(
            mediaUrl = "https://picsum.photos/400/600?random=3",
            mediaType = UiMediaType.AUDIO,
            duration = 314,
            isEditMode = true,
            onRemove = {},
        )
    }
}
