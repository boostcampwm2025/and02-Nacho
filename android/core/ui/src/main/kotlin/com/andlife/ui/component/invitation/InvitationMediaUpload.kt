package com.andlife.ui.component.invitation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoCardSize
import com.andlife.designsystem.theme.NachoIconSize
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.ui.R
import com.andlife.model.guestbook.UiMediaType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun InvitationMediaUpload(
    selectedMedias: ImmutableList<SelectedMedia>,
    currentMediaSizeBytes: Long,
    maxMediasCount: Int,
    maxMediaSizeBytes: Long,
    onMediaRemove: (SelectedMedia) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (selectedMedias.isNotEmpty()) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(NachoSpacing.small),
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(NachoSpacing.small),
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(selectedMedias) { media ->
                    SelectedMediaCard(
                        media = media,
                        onRemove = { onMediaRemove(media) },
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(NachoSpacing.xSmall)) {
                // 미디어 개수 표시
                Text(
                    text = "${selectedMedias.size}/${maxMediasCount}",
                    style = NachoTheme.typography.bodySmallRegular,
                    color = NachoTheme.colorScheme.textTertiary,
                    modifier = Modifier.padding(top = NachoSpacing.xSmall)
                )

                // 용량 표시
                Text(
                    text = "${currentMediaSizeBytes / (1024 * 1024)}/${maxMediaSizeBytes / (1024 * 1024)}MB",
                    style = NachoTheme.typography.bodySmallRegular,
                    color = NachoTheme.colorScheme.textTertiary,
                    modifier = Modifier.padding(top = NachoSpacing.xSmall)
                )
            }
        }
    }
}

@Composable
private fun SelectedMediaCard(
    media: SelectedMedia,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.size(NachoCardSize.media),
    ) {
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = NachoTheme.shapes.small,
        ) {
            SelectedMediaItem(media)
        }

        IconButton(
            onClick = onRemove,
            modifier =
                Modifier
                    .padding(NachoSpacing.xSmall)
                    .align(Alignment.TopEnd)
                    .size(NachoIconSize.medium),
        ) {
            Surface(
                modifier = modifier,
                shape = NachoTheme.shapes.small,
                color = NachoTheme.colorScheme.backgroundOverlay,
                contentColor = NachoTheme.colorScheme.textOnPrimary,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close_12),
                    contentDescription = stringResource(R.string.desc_remove_media),
                    tint = NachoTheme.colorScheme.brandOnPrimary,
                    modifier = Modifier.size(NachoIconSize.xSmall),
                )
            }
        }
    }
}

@PreviewTheme
@Composable
private fun InvitationMediaUploadPreview() {
    NachoTheme {
        InvitationMediaUpload(
            selectedMedias =
                listOf(
                    SelectedMedia(
                        uri = "https://via.placeholder.com/150",
                        type = UiMediaType.IMAGE,
                    ),
                    SelectedMedia(
                        uri = "https://via.placeholder.com/150",
                        type = UiMediaType.VIDEO,
                    ),
                    SelectedMedia(
                        uri = "https://via.placeholder.com/150",
                        type = UiMediaType.AUDIO,
                    ),
                ).toImmutableList(),
            onMediaRemove = {},
            currentMediaSizeBytes = 120 * 1024 * 1024L,
            maxMediasCount = 20,
            maxMediaSizeBytes = 500 * 1024 * 1024L,
        )
    }
}
