package com.andlife.ui.component.invitation

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
    onMediasSelected: (List<SelectedMedia>) -> Unit,
    onMediaRemove: (SelectedMedia) -> Unit,
    modifier: Modifier = Modifier,
    maxMedias: Int = 5,
) {
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetMultipleContents(),
        ) { uris ->
            val uriStrings = uris.map { it.toString() }
            val availableSlots = maxMedias - selectedMedias.size

            if (availableSlots <= 0) return@rememberLauncherForActivityResult

            val mediasToAdd =
                uriStrings
                    .take(availableSlots)
                    .map { uriToSelectedMedia(context, it) }

            onMediasSelected(selectedMedias + mediasToAdd)
        }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(InvitationSpacing.small),
    ) {
        if (selectedMedias.isEmpty()) {
            AddMediaCard(
                onClick = {
                    if (selectedMedias.size < maxMedias) {
                        launcher.launch("*/*")
                    }
                },
                enabled = selectedMedias.size < maxMedias,
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(InvitationSpacing.small),
                modifier = Modifier.fillMaxWidth(),
            ) {
                item {
                    AddMediaCard(
                        onClick = {
                            if (selectedMedias.size < maxMedias) {
                                launcher.launch("*/*")
                            }
                        },
                        enabled = selectedMedias.size < maxMedias,
                    )
                }

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
private fun AddMediaCard(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier.size(90.dp),
        enabled = enabled,
        shape = InvitationTheme.shapes.small,
        colors =
            CardDefaults.cardColors(
                containerColor =
                    if (enabled) {
                        InvitationTheme.colorScheme.backgroundSecondary
                    } else {
                        InvitationTheme.colorScheme.backgroundSecondary.copy(alpha = 0.5f)
                    },
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .aspectRatio(1f)
                    .fillMaxWidth()
                    .padding(InvitationSpacing.small),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add_24),
                contentDescription = stringResource(R.string.desc_add),
                tint =
                    if (enabled) {
                        InvitationTheme.colorScheme.iconPrimary
                    } else {
                        InvitationTheme.colorScheme.iconPrimary.copy(alpha = 0.5f)
                    },
                modifier = Modifier.size(InvitationIconSize.medium),
            )

            Spacer(modifier = Modifier.height(InvitationSpacing.xSmall))

            Text(
                text = stringResource(R.string.txt_add_media),
                style = InvitationTheme.typography.bodySmallRegular,
                color =
                    if (enabled) {
                        InvitationTheme.colorScheme.textSecondary
                    } else {
                        InvitationTheme.colorScheme.textSecondary.copy(alpha = 0.5f)
                    },
            )
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
            onMediasSelected = {},
            onMediaRemove = {},
        )
    }
}

private fun uriToSelectedMedia(
    context: Context,
    uriString: String,
): SelectedMedia {
    val uri = Uri.parse(uriString)

    val mimeType =
        try {
            context.contentResolver.getType(uri)
        } catch (e: Exception) {
            null
        }

    val mediaType =
        when {
            mimeType?.startsWith("image/") == true -> UiMediaType.IMAGE
            mimeType?.startsWith("video/") == true -> UiMediaType.VIDEO
            mimeType?.startsWith("audio/") == true -> UiMediaType.AUDIO
            else -> UiMediaType.IMAGE
        }

    val duration =
        if (mediaType == UiMediaType.VIDEO || mediaType == UiMediaType.AUDIO) {
            try {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(context, uri)
                retriever
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    ?.toLongOrNull()
                    ?.div(1000)
                    ?.toInt()
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }

    return SelectedMedia(
        uri = uriString,
        type = mediaType,
        duration = duration,
    )
}
