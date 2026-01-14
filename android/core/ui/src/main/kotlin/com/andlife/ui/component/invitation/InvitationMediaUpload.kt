package com.andlife.ui.component.invitation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.InvitationIconSize
import com.andlife.designsystem.theme.InvitationSpacing
import com.andlife.designsystem.theme.InvitationTheme
import com.andlife.ui.R
import com.andlife.ui.model.UiMediaType

@Composable
fun InvitationMediaUpload(
    selectedMedias: List<SelectedMedia>,
    onMediaRemove: (SelectedMedia) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(InvitationSpacing.small),
    ) {
        if (selectedMedias.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(InvitationSpacing.small),
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(selectedMedias) { media ->
                    SelectedMediaCard(
                        media = media,
                        onRemove = { onMediaRemove(media) },
                    )
                }
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
        modifier = modifier.size(90.dp),
    ) {
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = InvitationTheme.shapes.small,
        ) {
            SelectedMediaItem(media)
        }

        IconButton(
            onClick = onRemove,
            modifier =
                Modifier
                    .padding(InvitationSpacing.xSmall)
                    .align(Alignment.TopEnd)
                    .size(InvitationIconSize.medium),
        ) {
            Surface(
                modifier = modifier,
                shape = InvitationTheme.shapes.small,
                color = InvitationTheme.colorScheme.backgroundOverlay,
                contentColor = InvitationTheme.colorScheme.textOnPrimary,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close_12),
                    contentDescription = stringResource(R.string.desc_remove_media),
                    tint = InvitationTheme.colorScheme.brandOnPrimary,
                    modifier = Modifier.size(InvitationIconSize.xSmall),
                )
            }
        }
    }
}

@PreviewTheme
@Composable
private fun InvitationMediaUploadPreview() {
    InvitationTheme {
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
                ),
            onMediaRemove = {},
        )
    }
}
