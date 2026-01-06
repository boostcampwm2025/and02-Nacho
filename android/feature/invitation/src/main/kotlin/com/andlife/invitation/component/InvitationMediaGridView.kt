package com.andlife.invitation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.InvitationSpacing
import com.andlife.designsystem.theme.InvitationTheme
import com.andlife.domain.model.MediaType
import com.andlife.invitation.model.guestbook.InvitationCollectionUiModel
import com.andlife.invitation.util.toUiType
import com.andlife.ui.component.media.MediaItem
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun InvitationMediaGridView(
    items: List<InvitationCollectionUiModel>,
    onItemClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier,
        contentPadding = PaddingValues(InvitationSpacing.small),
        horizontalArrangement = Arrangement.spacedBy(InvitationSpacing.small),
        verticalArrangement = Arrangement.spacedBy(InvitationSpacing.small)
    ) {
        items(items = items) { item ->
            MediaItem(
                mediaUrl = item.url,
                mediaType = item.type,
                duration = item.durationSeconds,
                onClick = { onItemClick(item.id) }
            )
        }
    }
}

@PreviewTheme
@Composable
fun InvitationMediaGridViewPreview() {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    val mockItems = listOf(
        InvitationCollectionUiModel(
            id = 1L,
            url = "https://picsum.photos/400/600?random=1",
            type = MediaType.IMAGE.toUiType(),
            content = "방명록 내용 1",
            authorName = "사용자1",
            authorProfileUrl = null,
            createdAt = now,
            durationSeconds = null
        ),
        InvitationCollectionUiModel(
            id = 2L,
            url = "https://picsum.photos/400/600?random=2",
            type = MediaType.VIDEO.toUiType(),
            content = "방명록 내용 2",
            authorName = "사용자2",
            authorProfileUrl = null,
            createdAt = now,
            durationSeconds = 120
        ),
        InvitationCollectionUiModel(
            id = 3L,
            url = "https://picsum.photos/400/600?random=3",
            type = MediaType.AUDIO.toUiType(),
            content = "방명록 내용 3",
            authorName = "사용자3",
            authorProfileUrl = null,
            createdAt = now,
            durationSeconds = 300
        )
    )

    InvitationTheme {
        InvitationMediaGridView(
            items = mockItems,
            onItemClick = {}
        )
    }
}
