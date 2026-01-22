package com.andlife.ui.component.invitation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import coil3.compose.AsyncImage
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoIconSize
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.ui.R
import com.andlife.ui.component.media.MediaOverlay
import com.andlife.model.guestbook.UiMediaType
import com.andlife.ui.util.toFormatDuration

@Composable
fun SelectedMediaItem(
    media: SelectedMedia,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(NachoSpacing.small)),
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
                            .background(NachoTheme.colorScheme.backgroundSecondary),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_mic_16),
                        tint = NachoTheme.colorScheme.brandPrimary,
                        modifier = Modifier.size(NachoIconSize.large),
                        contentDescription = stringResource(R.string.desc_media_audio),
                    )
                }
            }

            UiMediaType.VIDEO -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = media.thumbnailUrl ?: media.uri,
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
                                .size(NachoIconSize.xLarge),
                        tint = NachoTheme.colorScheme.iconTertiary.copy(alpha = 0.8f),
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
                        .padding(NachoSpacing.small),
            )
        }
    }
}

@PreviewTheme
@Composable
private fun EditModeVideoItemPreview() {
    NachoTheme {
        SelectedMediaItem(
            SelectedMedia(
                1L,
                "https://picsum.photos/400/600?random=3",
                UiMediaType.VIDEO,
                828,
            ),
        )
    }
}

@PreviewTheme
@Composable
private fun EditModeAudioItemPreview() {
    NachoTheme {
        SelectedMediaItem(
            SelectedMedia(
                1L,
                "https://picsum.photos/400/600?random=3",
                UiMediaType.AUDIO,
                314,
            ),
        )
    }
}
