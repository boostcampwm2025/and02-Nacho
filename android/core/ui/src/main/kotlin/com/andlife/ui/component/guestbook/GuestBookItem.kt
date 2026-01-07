package com.andlife.ui.component.guestbook

import androidx.annotation.OptIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil3.compose.AsyncImage
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.InvitationSpacing
import com.andlife.designsystem.theme.InvitationStroke
import com.andlife.designsystem.theme.InvitationTheme
import com.andlife.ui.R
import com.andlife.ui.component.media.MediaOverlay
import com.andlife.ui.model.GuestBookEntryMediaUiModel
import com.andlife.ui.model.MediaType
import com.andlife.ui.player.VideoPlayerPool
import com.andlife.ui.util.toFormatDuration
import com.andlife.ui.util.toRelativeTimeString
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.LocalDateTime
import com.andlife.designsystem.R as designR

@Composable
fun GuestBookItem(
    authorName: String,
    createdAt: LocalDateTime,
    textContent: String,
    visualMediaUrls: ImmutableList<GuestBookEntryMediaUiModel>,
    audioMediaUrls: ImmutableList<GuestBookEntryMediaUiModel>,
    totalVisualCount: Int,
    modifier: Modifier = Modifier,
    authorProfileImageUrl: String? = null,
    invitationTitle: String? = null,
    invitationId: Long? = null,
    isAuthorSelf: Boolean = false,
    shouldPlayVideo: Boolean = false,
    onInvitationTitleClick: (Long) -> Unit = {},
    onVisualMediaClick: (GuestBookEntryMediaUiModel) -> Unit = {},
    onAudioMediaClick: (GuestBookEntryMediaUiModel) -> Unit = {},
    onMoreOptionsClick: () -> Unit = {}
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(InvitationSpacing.medium)
    ) {
        GuestBookItemHeader(
            authorName = authorName,
            authorProfileImageUrl = authorProfileImageUrl,
            createdAt = createdAt,
            isAuthorSelf = isAuthorSelf,
            onMoreOptionsClick = onMoreOptionsClick,
        )
        GuestBookItemTextContent(
            invitationId = invitationId,
            invitationTitle = invitationTitle,
            textContent = textContent,
            onInvitationTitleClick = onInvitationTitleClick,
        )
        if (visualMediaUrls.isNotEmpty()) {
            GuestBookItemVisualMediaSection(
                visualMediaUrls = visualMediaUrls,
                totalVisualCount = totalVisualCount,
                shouldPlayVideo = shouldPlayVideo,
                onVisualMediaClick = onVisualMediaClick,
            )
        }
        if (audioMediaUrls.isNotEmpty()) {
            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.spacedBy(InvitationSpacing.small),
            ) {
                audioMediaUrls.forEach { audio ->
                    GuestBookAudioItem(
                        audio = audio,
                        onAudioMediaClick = onAudioMediaClick
                    )
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(top = InvitationSpacing.xSmall),
            color = InvitationTheme.colorScheme.backgroundBorder,
        )
    }
}

@Composable
private fun GuestBookItemHeader(
    authorName: String,
    authorProfileImageUrl: String?,
    createdAt: LocalDateTime,
    isAuthorSelf: Boolean,
    onMoreOptionsClick: (() -> Unit),
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(InvitationSpacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = authorProfileImageUrl,
            error = painterResource(R.drawable.ic_error_outline_24),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(InvitationSpacing.xSmall),
        ) {
            Text(
                text = authorName,
                style = InvitationTheme.typography.bodyMediumMedium,
                color = InvitationTheme.colorScheme.textPrimary,
            )
            Text(
                text = createdAt.toRelativeTimeString(),
                style = InvitationTheme.typography.bodySmallRegular,
                color = InvitationTheme.colorScheme.textTertiary,
            )
        }
        if (isAuthorSelf) {
            IconButton(onClick = onMoreOptionsClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_more_vert_24),
                    contentDescription = stringResource(R.string.desc_edit_guest_book),
                    tint = InvitationTheme.colorScheme.textPrimary,
                )
            }
        }
    }
}

@Composable
private fun GuestBookItemTextContent(
    invitationId: Long?,
    invitationTitle: String?,
    textContent: String,
    onInvitationTitleClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(InvitationSpacing.medium),
    ) {
        invitationTitle?.let {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        invitationId?.let { id ->
                            onInvitationTitleClick(id)
                        }
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(InvitationSpacing.xSmall),
            ) {
                Text(
                    text = invitationTitle,
                    style = InvitationTheme.typography.bodyLargeMedium,
                    color = InvitationTheme.colorScheme.textPrimary,
                )
                Icon(
                    painter = painterResource(designR.drawable.ic_chevron_right_24),
                    contentDescription = stringResource(R.string.desc_move_to_invitation),
                    tint = InvitationTheme.colorScheme.textPrimary,
                )
            }
        }
        Text(
            text = textContent,
            style = InvitationTheme.typography.bodyMediumRegular,
            color = InvitationTheme.colorScheme.textPrimary,
        )
    }
}

@Composable
private fun GuestBookItemVisualMediaSection(
    visualMediaUrls: ImmutableList<GuestBookEntryMediaUiModel>,
    totalVisualCount: Int,
    shouldPlayVideo: Boolean,
    onVisualMediaClick: (GuestBookEntryMediaUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(pageCount = { visualMediaUrls.size })
    val context = LocalContext.current

    LaunchedEffect(pagerState.currentPage) {
        // 다음 비디오 미리 준비
        if (pagerState.currentPage < visualMediaUrls.lastIndex) {
            val nextMedia = visualMediaUrls[pagerState.currentPage + 1]
            if (nextMedia.type == MediaType.VIDEO) {
                VideoPlayerPool.preparePlayer(context, nextMedia.url)
            }
        }
        // 이전 비디오도 미리 준비하는 것도 고려
        if (pagerState.currentPage > 0) {
            val prevMedia = visualMediaUrls[pagerState.currentPage - 1]
            if (prevMedia.type == MediaType.VIDEO) {
                VideoPlayerPool.preparePlayer(context, prevMedia.url)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f) // TODO: 추후 미디어 비율에 맞게 조정 필요, 일단 정사각형으로 고정
            .clip(InvitationTheme.shapes.small),
    ) {
        HorizontalPager(state = pagerState) { page ->
            val media = visualMediaUrls[page]
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onVisualMediaClick(media) }
            ) {
                when (media.type) {
                    MediaType.VIDEO -> {
                        SimpleVideoPlayer(
                            videoUrl = media.url,
                            thumbnailUrl = media.thumbnailUrl,
                            shouldPlay = shouldPlayVideo && pagerState.currentPage == page,
                        )

                        media.durationSeconds?.let {
                            MediaOverlay(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(InvitationSpacing.small),
                                text = it.toFormatDuration() // TODO: 타이머 기능 추가해야 함.
                            )
                        }
                    }

                    else -> {
                        AsyncImage(
                            model = media.url,
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.Crop,
                        )
                    }
                }
            }
        }

        if (visualMediaUrls.size > 1) {
            MediaOverlay(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(InvitationSpacing.small),
                text = "${pagerState.currentPage + 1}/$totalVisualCount",
                shape = InvitationTheme.shapes.medium,
            )
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun SimpleVideoPlayer(
    videoUrl: String,
    thumbnailUrl: String?,
    shouldPlay: Boolean,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val videoPlayer = remember(videoUrl) {
        VideoPlayerPool.getPlayer(context, videoUrl)
    }

    var isVideoReady by remember(videoUrl) {
        mutableStateOf(videoPlayer.exoPlayer.playbackState == Player.STATE_READY)
    }

    DisposableEffect(videoPlayer) {
        isVideoReady = videoPlayer.exoPlayer.playbackState == Player.STATE_READY

        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                isVideoReady = playbackState == Player.STATE_READY ||
                    playbackState == Player.STATE_BUFFERING
            }

            override fun onRenderedFirstFrame() {
                isVideoReady = true
            }
        }

        videoPlayer.exoPlayer.addListener(listener)

        onDispose {
            videoPlayer.exoPlayer.removeListener(listener)
        }
    }

    DisposableEffect(videoUrl) {
        VideoPlayerPool.protectPlayer(videoUrl)
        onDispose {
            VideoPlayerPool.unprotectPlayer(videoUrl)
            VideoPlayerPool.pausePlayer(videoUrl)
        }
    }

    LaunchedEffect(shouldPlay) {
        if (shouldPlay) {
            VideoPlayerPool.playPlayer(videoUrl)
        } else {
            VideoPlayerPool.pausePlayer(videoUrl)
        }
    }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color.Black)
    ) {
        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    player = videoPlayer.exoPlayer
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                }
            },
            update = { playerView ->
                playerView.player = videoPlayer.exoPlayer
            },
            modifier = Modifier.fillMaxSize()
        )

        if (!isVideoReady && thumbnailUrl != null) {
            AsyncImage(
                model = thumbnailUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentScale = ContentScale.Fit,
            )
        }
    }
}

@Composable
private fun GuestBookAudioItem(
    audio: GuestBookEntryMediaUiModel,
    onAudioMediaClick: (GuestBookEntryMediaUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = InvitationTheme.colorScheme.brandLight,
                shape = InvitationTheme.shapes.small,
            )
            .padding(InvitationSpacing.large),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(InvitationSpacing.small),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = InvitationTheme.colorScheme.brandPrimary,
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_mic_filled_18),
                contentDescription = stringResource(R.string.desc_audio_media_icon),
                tint = InvitationTheme.colorScheme.iconTertiary,
            )
        }
        Column(
            modifier =
                Modifier.weight(1f)
        ) {
            Text(
                text = "오디오 제목", // TODO: 오디오 제목 필요
                style = InvitationTheme.typography.bodyMediumMedium,
                color = InvitationTheme.colorScheme.textPrimary,
            )
            Text(
                text = stringResource(
                    R.string.format_audio_duration,
                    audio.durationSeconds ?: 0
                ),
                style = InvitationTheme.typography.bodySmallRegular,
                color = InvitationTheme.colorScheme.textSecondary,
            )
        }
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = InvitationTheme.colorScheme.backgroundPrimary,
            border = BorderStroke(
                width = InvitationStroke.small,
                color = InvitationTheme.colorScheme.iconDisabled,
            ),
            onClick = { onAudioMediaClick(audio) }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(R.drawable.ic_play_arrow_24),
                    contentDescription = stringResource(R.string.desc_play_audio),
                    tint = InvitationTheme.colorScheme.textSecondary,
                )
            }
        }
    }
}

@PreviewTheme
@Composable
private fun GuestBookItemPreview() {
    InvitationTheme {
        LazyColumn(
            modifier = Modifier
                .padding(InvitationSpacing.large),
            verticalArrangement = Arrangement.spacedBy(InvitationSpacing.large),
        ) {
            item {
                GuestBookItem(
                    authorName = "홍길동",
                    createdAt = LocalDateTime(2024, 6, 1, 12, 0),
                    textContent = "축하합니다! 행복하세요!",
                    visualMediaUrls = listOf(
                        GuestBookEntryMediaUiModel(
                            id = 1L,
                            type = MediaType.IMAGE,
                            url = "https://via.placeholder.com/150",
                            thumbnailUrl = "https://via.placeholder.com/150",
                            durationSeconds = 34,
                            displayOrder = 0,
                        ),
                        GuestBookEntryMediaUiModel(
                            id = 2L,
                            type = MediaType.VIDEO,
                            url = "https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4",
                            thumbnailUrl = "https://via.placeholder.com/150",
                            durationSeconds = 30,
                            displayOrder = 1,
                        )
                    ).toImmutableList(),
                    audioMediaUrls = listOf(
                        GuestBookEntryMediaUiModel(
                            id = 3L,
                            type = MediaType.AUDIO,
                            url = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
                            thumbnailUrl = "https://via.placeholder.com/150",
                            durationSeconds = 45,
                            displayOrder = 0,
                        )
                    ).toImmutableList(),
                    totalVisualCount = 2,
                    authorProfileImageUrl = null,
                    invitationTitle = "우리 결혼해요!",
                    invitationId = 1001L,
                    isAuthorSelf = true,
                )
            }
        }
    }
}
