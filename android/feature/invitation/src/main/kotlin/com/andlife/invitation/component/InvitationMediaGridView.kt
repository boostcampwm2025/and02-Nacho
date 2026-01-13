package com.andlife.invitation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.InvitationTheme
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.domain.model.guestbook.MediaType
import com.andlife.invitation.model.guestbook.collection.InvitationCollectionUiModel
import com.andlife.invitation.util.toUiType
import com.andlife.ui.component.media.MediaItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun InvitationMediaGridView(
    items: ImmutableList<InvitationCollectionUiModel>,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = NachoTheme.colorScheme.backgroundPrimary
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = modifier,
            contentPadding = PaddingValues(NachoSpacing.small),
            horizontalArrangement = Arrangement.spacedBy(NachoSpacing.small),
            verticalArrangement = Arrangement.spacedBy(NachoSpacing.small),
        ) {
            itemsIndexed(
                items = items,
                key = { index, item -> "${item.type}_${item.id}" },
            ) { index, item ->
                MediaItem(
                    mediaUrl = item.url,
                    mediaType = item.type,
                    duration = item.durationSeconds,
                    onClick = { onItemClick(index) },
                )
            }
        }
    }
}

@PreviewTheme
@Composable
private fun InvitationMediaGridViewPreview() {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    val mockItems =
        persistentListOf(
            InvitationCollectionUiModel(
                id = 1L,
                url = "https://picsum.photos/400/600?random=1",
                type = MediaType.IMAGE.toUiType(),
                content = "방명록 내용 1",
                authorName = "사용자1",
                authorProfileUrl = null,
                createdAt = now,
                durationSeconds = null,
            ),
            InvitationCollectionUiModel(
                id = 2L,
                url = "https://picsum.photos/400/600?random=2",
                type = MediaType.VIDEO.toUiType(),
                content = "방명록 내용 2",
                authorName = "사용자2",
                authorProfileUrl = null,
                createdAt = now,
                durationSeconds = 120,
            ),
            InvitationCollectionUiModel(
                id = 3L,
                url = "https://picsum.photos/400/600?random=3",
                type = MediaType.AUDIO.toUiType(),
                content = "방명록 내용 3",
                authorName = "사용자3",
                authorProfileUrl = null,
                createdAt = now,
                durationSeconds = 300,
            ),
        )

    InvitationTheme {
        InvitationMediaGridView(
            items = mockItems,
            onItemClick = {},
        )
    }
}
