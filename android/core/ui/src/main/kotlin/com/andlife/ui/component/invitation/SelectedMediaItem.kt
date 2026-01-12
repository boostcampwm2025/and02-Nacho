package com.andlife.ui.component.invitation

import androidx.compose.foundation.background
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import coil3.compose.AsyncImage
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.InvitationIconSize
import com.andlife.designsystem.theme.InvitationSpacing
import com.andlife.designsystem.theme.InvitationTheme
import com.andlife.ui.R
import com.andlife.ui.component.media.MediaOverlay
import com.andlife.ui.model.UiMediaType
import com.andlife.ui.util.toFormatDuration

@Composable
fun SelectedMediaItem(
    media: SelectedMedia,
    modifier: Modifier = Modifier,
    onRemove: (() -> Unit)? = null,
) {
    Box(
        modifier =
            modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(InvitationSpacing.small)),
    ) {
        when (media.type) {
            UiMediaType.IMAGE -> {
                AsyncImage(
                    model = media.uri,
                    contentDescription = stringResource(R.string.desc_media_image),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.ic_image_24),
                    error = painterResource(R.drawable.ic_error_image_24),
                )
            }

            UiMediaType.AUDIO -> {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(InvitationTheme.colorScheme.backgroundSecondary),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_mic_24),
                        tint = InvitationTheme.colorScheme.brandPrimary,
                        modifier = Modifier.size(InvitationIconSize.large),
                        contentDescription = stringResource(R.string.desc_media_audio),
                    )
                }
            }

            UiMediaType.VIDEO -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = media.uri,
                        contentDescription = stringResource(R.string.desc_media_video),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.ic_image_24),
                        error = painterResource(R.drawable.ic_error_image_24),
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_play_circle_24),
                        contentDescription = stringResource(R.string.desc_ic_play),
                        modifier =
                            Modifier
                                .align(Alignment.Center)
                                .size(InvitationIconSize.xLarge),
                        tint = InvitationTheme.colorScheme.iconTertiary.copy(alpha = 0.8f),
                    )
                }
            }
        }

        if (media.duration != null && media.duration > 0) {
            MediaOverlay(
                text = media.duration.toFormatDuration(),
                modifier =
                    Modifier
                        .align(Alignment.BottomStart)
                        .padding(InvitationSpacing.small),
            )
        }

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
                contentDescription = null,
                tint = InvitationTheme.colorScheme.backgroundOverlay,
            )
        }
    }
}

@PreviewTheme
@Composable
private fun EditModeVideoItemPreview() {
    InvitationTheme {
        SelectedMediaItem(
            SelectedMedia(
                "https://picsum.photos/400/600?random=3",
                UiMediaType.VIDEO,
                828,
            ),
            onRemove = {},
        )
    }
}

@PreviewTheme
@Composable
private fun EditModeAudioItemPreview() {
    InvitationTheme {
        SelectedMediaItem(
            SelectedMedia(
                "https://picsum.photos/400/600?random=3",
                UiMediaType.AUDIO,
                314,
            ),
            onRemove = {},
        )
    }
}
