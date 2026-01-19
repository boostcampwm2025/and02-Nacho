package com.andlife.invitation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
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
    var isVideoReady by remember(exoPlayer) { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(exoPlayer, lifecycleOwner) {
        if (exoPlayer == null) return@DisposableEffect onDispose {}

        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    isVideoReady = true
                }
            }
        }

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    exoPlayer.pause()
                }
                Lifecycle.Event.ON_RESUME -> {
                    exoPlayer.play()
                }
                else -> {}
            }
        }

        exoPlayer.addListener(listener)
        lifecycleOwner.lifecycle.addObserver(observer)

        if (exoPlayer.playbackState == Player.STATE_READY) {
            isVideoReady = true
        }

        onDispose {
            exoPlayer.removeListener(listener)
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NachoTheme.colorScheme.backgroundOverlay),
    ) {
        if (item.type == UiMediaType.IMAGE || !item.thumbnailUrl.isNullOrEmpty()) {
            AsyncImage(
                model = if (item.type == UiMediaType.IMAGE) item.mediaUrl else item.thumbnailUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }

        if (exoPlayer != null) {
            when (item.type) {
                UiMediaType.VIDEO -> {
                    VideoPlayer(
                        exoPlayer = exoPlayer,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                alpha = if (isVideoReady) 1f else 0f
                            },
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
