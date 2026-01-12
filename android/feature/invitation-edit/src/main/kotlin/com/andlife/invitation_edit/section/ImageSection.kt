package com.andlife.invitation_edit.section

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.InvitationSpacing
import com.andlife.designsystem.theme.InvitationTheme
import com.andlife.invitation_edit.R
import com.andlife.invitation_edit.component.RemovableImage
import com.andlife.invitation_edit.model.create.ThumbnailImageUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun ImageSection(
    imageList: ImmutableList<ThumbnailImageUiModel>,
    onAddImageClick: () -> Unit,
    onRemoveClick: (ThumbnailImageUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.background(InvitationTheme.colorScheme.backgroundPrimary),
        verticalArrangement = Arrangement.spacedBy(InvitationSpacing.small),
    ) {
        Text(
            text = stringResource(R.string.txt_add_image),
            style = InvitationTheme.typography.bodyMediumSemiBold,
            color = InvitationTheme.colorScheme.textPrimary,
            modifier = Modifier.padding(start = InvitationSpacing.large, top = InvitationSpacing.large),
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = InvitationSpacing.large),
            horizontalArrangement = Arrangement.spacedBy(InvitationSpacing.small),
        ) {
            item {
                AddImageButton(
                    onClick = onAddImageClick,
                    modifier =
                        Modifier
                            .fillParentMaxWidth(1f / 3f)
                            .aspectRatio(1f),
                )
            }
            items(items = imageList, key = { it.id }) {
                RemovableImage(
                    imageUrl = it.url,
                    onRemoveClick = { onRemoveClick(it) },
                    modifier =
                        Modifier
                            .fillParentMaxWidth(1f / 3f)
                            .aspectRatio(1f),
                )
            }
        }
        Text(
            text = stringResource(R.string.txt_add_image_desc),
            style = InvitationTheme.typography.bodySmallMedium,
            color = InvitationTheme.colorScheme.textTertiary,
            modifier = Modifier.padding(start = InvitationSpacing.large, bottom = InvitationSpacing.large),
        )
    }
}

@Composable
private fun AddImageButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = InvitationTheme.shapes.small,
        border = BorderStroke(InvitationSpacing.twoXSmall, InvitationTheme.colorScheme.backgroundBorder),
        modifier = modifier,
        onClick = onClick,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(InvitationTheme.colorScheme.backgroundTertiary),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.desc_add_image),
                tint = InvitationTheme.colorScheme.backgroundBorder,
            )
            Text(
                text = stringResource(R.string.txt_add_image),
                style = InvitationTheme.typography.bodySmallMedium,
                color = InvitationTheme.colorScheme.backgroundBorder,
            )
        }
    }
}

@Composable
@PreviewTheme
private fun ImageSectionPreview() {
    ImageSection(
        imageList = persistentListOf(),
        onAddImageClick = {},
        onRemoveClick = {},
    )
}
