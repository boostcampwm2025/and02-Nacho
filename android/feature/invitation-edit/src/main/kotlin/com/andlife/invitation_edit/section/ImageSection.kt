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
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
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
    isLoading: Boolean = false,
) {
    Column(
        modifier = modifier.background(NachoTheme.colorScheme.backgroundPrimary),
        verticalArrangement = Arrangement.spacedBy(NachoSpacing.small),
    ) {
        Text(
            text = stringResource(R.string.txt_add_image),
            style = NachoTheme.typography.bodyMediumSemiBold,
            color = NachoTheme.colorScheme.textPrimary,
            modifier = Modifier.padding(start = NachoSpacing.large, top = NachoSpacing.large),
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = NachoSpacing.large),
            horizontalArrangement = Arrangement.spacedBy(NachoSpacing.small),
        ) {
            item {
                AddImageButton(
                    onClick = onAddImageClick,
                    isLoading = isLoading,
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
            style = NachoTheme.typography.bodySmallMedium,
            color = NachoTheme.colorScheme.textTertiary,
            modifier = Modifier.padding(start = NachoSpacing.large, bottom = NachoSpacing.large),
        )
    }
}

@Composable
private fun AddImageButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {
    Surface(
        shape = NachoTheme.shapes.small,
        border = BorderStroke(NachoSpacing.twoXSmall, NachoTheme.colorScheme.backgroundBorder),
        modifier = modifier,
        enabled = !isLoading,
        onClick = onClick,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(NachoTheme.colorScheme.backgroundTertiary),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.desc_add_image),
                tint = NachoTheme.colorScheme.backgroundBorder,
            )
            Text(
                text = stringResource(R.string.txt_add_image),
                style = NachoTheme.typography.bodySmallMedium,
                color = NachoTheme.colorScheme.backgroundBorder,
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
