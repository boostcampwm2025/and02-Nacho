package com.andlife.ui.component.collection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.model.guestbook.UiMediaType
import com.andlife.model.collection.CollectionUiModel
import com.andlife.ui.component.media.MediaItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import com.andlife.ui.R

@Composable
fun NachoMediaGridView(
    items: ImmutableList<CollectionUiModel>,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = NachoTheme.colorScheme.backgroundPrimary,
    ) {
        if (items.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.txt_collection_empty)
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = modifier,
                contentPadding = PaddingValues(NachoSpacing.small),
                horizontalArrangement = Arrangement.spacedBy(NachoSpacing.twoXSmall),
                verticalArrangement = Arrangement.spacedBy(NachoSpacing.twoXSmall),
            ) {
                itemsIndexed(
                    items = items,
                    key = { index, item -> "${item.type}_${item.id}" },
                ) { index, item ->
                    MediaItem(
                        mediaUrl = item.mediaUrl,
                        thumbnailUrl = item.thumbnailUrl,
                        mediaType = item.type,
                        duration = item.durationSeconds,
                        onClick = { onItemClick(index) },
                    )
                }
            }
        }
    }
}

@PreviewTheme
@Composable
private fun NachoMediaGridViewPreview() {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    val mockItems =
        persistentListOf(
            CollectionUiModel(
                id = 1L,
                mediaUrl = "https://picsum.photos/400/600?random=1",
                type = UiMediaType.IMAGE,
                content = "방명록 내용 1",
                authorName = "사용자1",
                authorProfileUrl = null,
                createdAt = now,
                durationSeconds = null,
            ),
            CollectionUiModel(
                id = 2L,
                mediaUrl = "https://picsum.photos/400/600?random=2",
                type = UiMediaType.VIDEO,
                content = "방명록 내용 2",
                authorName = "사용자2",
                authorProfileUrl = null,
                createdAt = now,
                durationSeconds = 120,
            ),
            CollectionUiModel(
                id = 3L,
                mediaUrl = "https://picsum.photos/400/600?random=3",
                type = UiMediaType.AUDIO,
                content = "방명록 내용 3",
                authorName = "사용자3",
                authorProfileUrl = null,
                createdAt = now,
                durationSeconds = 300,
            ),
        )

    NachoTheme {
        NachoMediaGridView(
            items = mockItems,
            onItemClick = {},
        )
    }
}
