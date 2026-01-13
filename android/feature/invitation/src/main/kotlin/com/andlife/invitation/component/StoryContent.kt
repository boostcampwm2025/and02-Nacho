package com.andlife.invitation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.InvitationTheme
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.domain.model.guestbook.MediaType
import com.andlife.invitation.model.guestbook.collection.InvitationCollectionUiModel
import com.andlife.invitation.util.toUiType
import com.andlife.ui.model.UiMediaType
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun StoryContent(
    item: InvitationCollectionUiModel,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(NachoTheme.colorScheme.backgroundOverlay),
    ) {
        when (item.type) {
            UiMediaType.IMAGE -> {
                AsyncImage(
                    model = item.url,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }
            UiMediaType.VIDEO -> {
                // TODO: VideoPlayer 컴포넌트 구현 (ExoPlayer)
                Box(modifier = Modifier.fillMaxSize().background(NachoTheme.colorScheme.backgroundOverlay))
            }
            UiMediaType.AUDIO -> {
            }
        }
        StoryTextSection(
            content = item.content,
            isExpanded = isExpanded,
            onToggleExpand = onToggleExpand,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@PreviewTheme
@Composable
private fun StoryContentPreview() {
    InvitationTheme {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        StoryContent(
            item =
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
            isExpanded = false,
            onToggleExpand = {},
        )
    }
}
