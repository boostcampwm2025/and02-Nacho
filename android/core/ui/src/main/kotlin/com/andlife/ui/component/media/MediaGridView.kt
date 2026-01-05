package com.andlife.ui.component.media

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
import com.andlife.domain.model.GuestBookMedia
import com.andlife.domain.model.MediaType
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun MediaGridView(
    items: List<GuestBookMedia>,
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
fun MediaGridViewPreview() {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    val mockItems = listOf(
        GuestBookMedia(
            id = 1L,
            url = "https://picsum.photos/400/600?random=1",
            type = MediaType.IMAGE,
            content = "방명록 내용 1",
            authorName = "사용자1",
            authorProfileUrl = null,
            createdAt = now,
            durationSeconds = null
        ),
        GuestBookMedia(
            id = 2L,
            url = "https://picsum.photos/400/600?random=2",
            type = MediaType.VIDEO,
            content = "방명록 내용 2",
            authorName = "사용자2",
            authorProfileUrl = null,
            createdAt = now,
            durationSeconds = 120
        ),
        GuestBookMedia(
            id = 3L,
            url = "https://picsum.photos/400/600?random=3",
            type = MediaType.AUDIO,
            content = "방명록 내용 3",
            authorName = "사용자3",
            authorProfileUrl = null,
            createdAt = now,
            durationSeconds = 300
        )
    )

    InvitationTheme {
        MediaGridView(
            items = mockItems,
            onItemClick = {}
        )
    }
}
