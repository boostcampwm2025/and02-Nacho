package com.andlife.invitation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import coil3.compose.AsyncImage
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.domain.model.guestbook.MediaType
import com.andlife.invitation.model.guestbook.collection.InvitationCollectionUiModel
import com.andlife.invitation.util.toUiType
import com.andlife.ui.component.media.AudioPlayer
import com.andlife.ui.component.media.VideoPlayer
import com.andlife.ui.model.UiMediaType
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun StoryContent(
    item: InvitationCollectionUiModel,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    exoPlayer: Player?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NachoTheme.colorScheme.backgroundOverlay),
    ) {
        when (item.type) {
            UiMediaType.IMAGE -> {
                AsyncImage(
                    model = item.mediaUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }
            UiMediaType.VIDEO -> {
                if (!item.thumbnailUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = item.thumbnailUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }
            }
            else -> { /* AUDIO는 기본 배경 필요 없음 */ }
        }

        if (exoPlayer != null) {
            when (item.type) {
                UiMediaType.VIDEO -> {
                    VideoPlayer(
                        exoPlayer = exoPlayer,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                UiMediaType.AUDIO -> {
                    AudioPlayer(
                        exoPlayer = exoPlayer,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                else -> { /* IMAGE는 플레이어 필요 없음 */ }
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
    NachoTheme {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val context = LocalContext.current
        val dummyPlayer = remember {
            ExoPlayer.Builder(context).build()
        }

        StoryContent(
            item =
                InvitationCollectionUiModel(
                    id = 3L,
                    mediaUrl = "https://picsum.photos/400/600?random=3",
                    type = MediaType.AUDIO.toUiType(),
                    content = "방명록 내용 3",
                    authorName = "사용자3",
                    authorProfileUrl = null,
                    createdAt = now,
                    durationSeconds = 300,
                ),
            isExpanded = false,
            exoPlayer = dummyPlayer,
            onToggleExpand = {},
        )
    }
}
