package com.andlife.ui.component.guestbook

import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoIconSize
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoStroke
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.media.audio.AudioPlaybackState
import com.andlife.media.video.AutoVideoPlayer
import com.andlife.media.video.AutoVideoPlayerPool
import com.andlife.media.video.FakeAutoVideoPlayerPool
import com.andlife.model.common.AuthorUiModel
import com.andlife.model.guestbook.GuestBookInvitationUiModel
import com.andlife.model.guestbook.GuestBookMediaUiModel
import com.andlife.model.guestbook.GuestBookUiModel
import com.andlife.model.guestbook.MediaUiType
import com.andlife.ui.R
import com.andlife.ui.component.icon.PlayerThumbnailIcon
import com.andlife.ui.component.media.MediaOverlay
import com.andlife.ui.component.media.video.PlayerControlOverlay
import com.andlife.ui.util.toFormatDuration
import com.andlife.ui.util.toRelativeTimeString
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDateTime
import com.andlife.designsystem.R as designR

private const val SAMPLE_INVITATION_ID = 1L

@Composable
fun GuestBookItem(
    guestBook: GuestBookUiModel,
    audioPlaybackState: AudioPlaybackState,
    videoPlayerPool: AutoVideoPlayerPool,
    onVisualMediaClick: (GuestBookMediaUiModel) -> Unit,
    onAudioMediaClick: (GuestBookMediaUiModel) -> Unit,
    onPlayVideoClick: (String) -> Unit,
    onFullscreenClick: (String, String?, Rect) -> Unit = { _, _, _ -> }, // TODO: 기본값 제거
    modifier: Modifier = Modifier,
    shouldPlayVideo: Boolean = false,
    isFromInvitationDetail: Boolean = true,
    isEditing: Boolean = false,
    isFullscreen: Boolean = false,
    onEditClick: (GuestBookUiModel) -> Unit = {},
    onDeleteClick: (GuestBookUiModel) -> Unit = {},
    onReportClick: (Long) -> Unit = {},
    onInvitationTitleClick: (Long) -> Unit? = {},
) {
    val backgroundColor = if (isEditing) {
        NachoTheme.colorScheme.brandLight
    } else {
        NachoTheme.colorScheme.backgroundPrimary
    }

    val maxTextLines = when {
        guestBook.visualMedias.isNotEmpty() -> 2
        guestBook.audioMedias.isNotEmpty() -> 4
        else -> 6
    }

    val canShowMenuButton = if (isFromInvitationDetail) {
        guestBook.id != SAMPLE_INVITATION_ID
    } else {
        !(guestBook.id == SAMPLE_INVITATION_ID || guestBook.isOwner)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor),
        verticalArrangement = Arrangement.spacedBy(NachoSpacing.medium),
    ) {
        GuestBookItemHeader(
            author = guestBook.author,
            createdAt = guestBook.createdAt,
            canShowMenuButton = canShowMenuButton,
            canEdit = guestBook.isOwner && isFromInvitationDetail,
            canDelete = (guestBook.isOwner || guestBook.isInvitationOwner) && isFromInvitationDetail,
            canReport = !guestBook.isOwner,
            onEditClick = { onEditClick(guestBook) },
            onDeleteClick = { onDeleteClick(guestBook) },
            onReportClick = { onReportClick(guestBook.id) },
        )
        GuestBookItemTextSection(
            invitation = guestBook.invitation,
            textContent = guestBook.textContent,
            maxLines = maxTextLines,
            onInvitationTitleClick = onInvitationTitleClick,
        )
        GuestBookItemVisualMediaSection(
            guestBookId = guestBook.id,
            visualMediaUrls = guestBook.visualMedias,
            totalVisualCount = guestBook.totalVisualCount,
            shouldPlayVideo = shouldPlayVideo,
            isFullscreen = isFullscreen,
            videoPlayerPool = videoPlayerPool,
            onVisualMediaClick = onVisualMediaClick,
            onPlayVideoClick = onPlayVideoClick,
            onFullscreenClick = onFullscreenClick
        )
        GuestBookItemAudioSection(
            audioMedias = guestBook.audioMedias,
            audioPlaybackState = audioPlaybackState,
            onAudioMediaClick = onAudioMediaClick,
        )
        HorizontalDivider(
            modifier = Modifier
                .padding(horizontal = NachoSpacing.large)
                .padding(top = NachoSpacing.xSmall),
            color = NachoTheme.colorScheme.backgroundBorder,
        )
    }
}

@Composable
private fun GuestBookItemHeader(
    author: AuthorUiModel,
    createdAt: LocalDateTime,
    canShowMenuButton: Boolean,
    canEdit: Boolean,
    canDelete: Boolean,
    canReport: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onReportClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                top = NachoSpacing.large,
                start = NachoSpacing.large,
                end = NachoSpacing.xSmall
            ),
        horizontalArrangement = Arrangement.spacedBy(NachoSpacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = author.profileImageUrl,
            contentDescription = stringResource(R.string.desc_author_profile_image),
            modifier =
                Modifier
                    .size(40.dp)
                    .clip(CircleShape),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.ic_image_24),
            error = painterResource(R.drawable.ic_error_image_24),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(NachoSpacing.xSmall),
        ) {
            Text(
                text = author.name,
                style = NachoTheme.typography.bodyMediumMedium,
                color = NachoTheme.colorScheme.textPrimary,
            )
            Text(
                text = createdAt.toRelativeTimeString(),
                style = NachoTheme.typography.bodySmallRegular,
                color = NachoTheme.colorScheme.textTertiary,
            )
        }
        if (canShowMenuButton) {
            Box {
                IconButton(onClick = { isMenuExpanded = true }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_more_vert_24),
                        contentDescription = stringResource(R.string.desc_edit_guest_book),
                        tint = NachoTheme.colorScheme.textPrimary,
                    )
                }
                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false },
                    modifier = Modifier.width(200.dp),
                    containerColor = NachoTheme.colorScheme.backgroundPrimary,
                    shape = NachoTheme.shapes.medium,
                ) {
                    if (canEdit) {
                        Text(
                            text = stringResource(R.string.txt_label_edit),
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        isMenuExpanded = false
                                        onEditClick()
                                    }
                                    .padding(NachoSpacing.large),
                            style = NachoTheme.typography.bodyMediumMedium,
                            color = NachoTheme.colorScheme.textPrimary,
                        )
                    }
                    if (canDelete) {
                        Text(
                            text = stringResource(R.string.txt_label_delete),
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        isMenuExpanded = false
                                        onDeleteClick()
                                    }
                                    .padding(NachoSpacing.large),
                            style = NachoTheme.typography.bodyMediumMedium,
                            color = NachoTheme.colorScheme.textPrimary,
                        )
                    }
                    if (canReport) {
                        Text(
                            text = stringResource(R.string.txt_label_report),
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        isMenuExpanded = false
                                        onReportClick(author.id)
                                    }
                                    .padding(NachoSpacing.large),
                            style = NachoTheme.typography.bodyMediumMedium,
                            color = NachoTheme.colorScheme.textPrimary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GuestBookItemTextSection(
    invitation: GuestBookInvitationUiModel?,
    textContent: String,
    maxLines: Int,
    onInvitationTitleClick: (Long) -> Unit?,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }
    var isOverflowed by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = NachoSpacing.large),
        verticalArrangement = Arrangement.spacedBy(NachoSpacing.medium),
    ) {
        invitation?.title?.let { title ->
            Row(
                modifier =
                    Modifier
                        .clickable { onInvitationTitleClick(invitation.id) }
                        .padding(vertical = NachoSpacing.xSmall),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(NachoSpacing.xSmall),
            ) {
                Text(
                    text = title,
                    style = NachoTheme.typography.bodyLargeMedium,
                    color = NachoTheme.colorScheme.textPrimary,
                )
                Icon(
                    painter = painterResource(designR.drawable.ic_chevron_right_24),
                    contentDescription = stringResource(R.string.desc_move_to_invitation),
                    tint = NachoTheme.colorScheme.textPrimary,
                )
            }
        }

        if (textContent.isBlank()) return@Column

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .then(
                        if (isOverflowed) {
                            Modifier.clickable { isExpanded = !isExpanded }
                        } else {
                            Modifier
                        }
                    ),
            verticalArrangement = Arrangement.spacedBy(NachoSpacing.small),
        ) {
            Text(
                text = textContent,
                style = NachoTheme.typography.bodyMediumRegular,
                color = NachoTheme.colorScheme.textPrimary,
                maxLines = if (isExpanded) Int.MAX_VALUE else maxLines,
                overflow = TextOverflow.Ellipsis,
                onTextLayout = { textLayoutResult ->
                    if (!isExpanded) {
                        isOverflowed = textLayoutResult.hasVisualOverflow
                    }
                },
            )
            if (isOverflowed) {
                Text(
                    text =
                        if (isExpanded) {
                            stringResource(
                                R.string.txt_show_less,
                            )
                        } else {
                            stringResource(R.string.txt_show_more)
                        },
                    style = NachoTheme.typography.bodyMediumSemiBold,
                    color = NachoTheme.colorScheme.brandPrimary,
                )
            }
        }
    }
}

@Composable
private fun GuestBookItemVisualMediaSection(
    guestBookId: Long,
    visualMediaUrls: ImmutableList<GuestBookMediaUiModel>,
    totalVisualCount: Int,
    shouldPlayVideo: Boolean,
    isFullscreen: Boolean,
    videoPlayerPool: AutoVideoPlayerPool,
    onVisualMediaClick: (GuestBookMediaUiModel) -> Unit,
    onPlayVideoClick: (String) -> Unit,
    onFullscreenClick: (String, String?, Rect) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (visualMediaUrls.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { visualMediaUrls.size })
    var beyondViewportPageCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(pagerState.currentPage) {
        val nextPage = pagerState.currentPage + 1
        if (nextPage < visualMediaUrls.size) {
            val nextMedia = visualMediaUrls[nextPage]
            beyondViewportPageCount = if (nextMedia.type == MediaUiType.IMAGE) 1 else 0
        } else {
            beyondViewportPageCount = 0
        }
    }

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = NachoSpacing.large)
                .aspectRatio(1f) // TODO: 추후 미디어 비율에 맞게 조정 필요, 일단 정사각형으로 고정
                .clip(NachoTheme.shapes.small),
    ) {
        HorizontalPager(
            state = pagerState,
            beyondViewportPageCount = beyondViewportPageCount,
        ) { page ->
            val media = visualMediaUrls[page]
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable { onVisualMediaClick(media) },
            ) {
                when (media.type) {
                    MediaUiType.VIDEO -> {
                        VideoPlayerContainer(
                            guestBookId = guestBookId,
                            videoUrl = media.url,
                            thumbnailUrl = media.thumbnailUrl,
                            totalDurationSeconds = media.durationSeconds,
                            shouldPlay = shouldPlayVideo && pagerState.currentPage == page,
                            isFullscreen = isFullscreen,
                            videoPlayerPool = videoPlayerPool,
                            onPlayVideoClick = onPlayVideoClick,
                            onFullscreenClick = onFullscreenClick
                        )
                    }

                    else -> {
                        SubcomposeAsyncImage(
                            model = media.url,
                            contentDescription = stringResource(R.string.desc_guest_book_image_media),
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.Crop,
                            loading = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(NachoTheme.colorScheme.backgroundSecondary),
                                )
                            },
                            error = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(NachoTheme.colorScheme.backgroundSecondary),
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_error_image_24),
                                        contentDescription = stringResource(R.string.desc_error_image),
                                        tint = NachoTheme.colorScheme.iconDisabled,
                                        modifier =
                                            Modifier
                                                .size(60.dp)
                                                .align(Alignment.Center),
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }

        if (visualMediaUrls.size > 1) {
            MediaOverlay(
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(NachoSpacing.small),
                text = "${pagerState.currentPage + 1}/$totalVisualCount",
                shape = NachoTheme.shapes.medium,
            )
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun VideoPlayerContainer(
    guestBookId: Long,
    videoUrl: String,
    thumbnailUrl: String?,
    totalDurationSeconds: Int?,
    shouldPlay: Boolean,
    isFullscreen: Boolean,
    videoPlayerPool: AutoVideoPlayerPool,
    onFullscreenClick: (String, String?, Rect) -> Unit,
    onPlayVideoClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isVideoReady by remember(videoUrl) { mutableStateOf(false) }
    var currentPositionMs by remember(videoUrl) { mutableLongStateOf(0L) }
    var totalDurationMs by remember(videoUrl) { mutableLongStateOf((totalDurationSeconds?.times(1000L)) ?: 0L) }
    var isControlVisible by remember(videoUrl) { mutableStateOf(false) }
    var isSeeking by remember(videoUrl) { mutableStateOf(false) }
    var seekPositionMs by remember(videoUrl) { mutableLongStateOf(0L) }
    val isMuted by videoPlayerPool.isMuted.collectAsStateWithLifecycle()
    var containerBounds by remember { mutableStateOf(Rect.Zero) }

    val displayPositionMs = if (isSeeking) seekPositionMs else currentPositionMs
    val progress = if (totalDurationMs > 0) {
        (displayPositionMs.toFloat() / totalDurationMs.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }
    val timeText = "${(displayPositionMs / 1000).toInt().toFormatDuration()} / ${
        (totalDurationMs / 1000).toInt().toFormatDuration()
    }"

    LaunchedEffect(shouldPlay, videoUrl) {
        if (shouldPlay) {
            videoPlayerPool.playPlayer(videoUrl, guestBookId)
        } else {
            videoPlayerPool.pausePlayer(videoUrl)
        }
    }

    LaunchedEffect(isControlVisible) {
        if (isControlVisible) {
            delay(3000L)
            isControlVisible = false
        }
    }

    LaunchedEffect(shouldPlay) {
        if (!shouldPlay) isControlVisible = false
    }

    if (shouldPlay) {
        val currentPlayer = remember(videoUrl) { videoPlayerPool.getPlayer(videoUrl) }

        DisposableEffect(currentPlayer, videoUrl) {
            val listener = object : Player.Listener {
                override fun onRenderedFirstFrame() {
                    isVideoReady = true
                }

                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_READY && currentPlayer.exoPlayer.playWhenReady) {
                        isVideoReady = true
                        totalDurationMs = currentPlayer.exoPlayer.duration.coerceAtLeast(0L)
                    }
                }
            }
            currentPlayer.exoPlayer.addListener(listener)
            if (currentPlayer.exoPlayer.playbackState == Player.STATE_READY) {
                isVideoReady = true
                totalDurationMs = currentPlayer.exoPlayer.duration.coerceAtLeast(0L)
            }
            onDispose {
                currentPlayer.exoPlayer.removeListener(listener)
            }
        }

        LaunchedEffect(isVideoReady) {
            if (!isVideoReady) {
                currentPositionMs = 0L
                return@LaunchedEffect
            }
            while (true) {
                if (!isSeeking) {
                    currentPositionMs = currentPlayer.exoPlayer.currentPosition.coerceAtLeast(0L)
                }
                delay(250L)
            }
        }

        VideoPlayerContent(
            currentPlayer = currentPlayer,
            thumbnailUrl = thumbnailUrl,
            progress = progress,
            timeText = timeText,
            isControlVisible = isControlVisible,
            isMuted = isMuted,
            isVideoReady = isVideoReady,
            isFullscreen = isFullscreen,
            onVideoClick = { isControlVisible = !isControlVisible },
            onPlayVideoClick = { onPlayVideoClick(videoUrl) },
            onContainerPositioned = { containerBounds = it },
            onSeekValueChange = { newValue ->
                if (!isSeeking) currentPlayer.pause()
                isSeeking = true
                seekPositionMs = (newValue * totalDurationMs).toLong()
            },
            onSeekValueChangeFinished = {
                currentPlayer.seekTo(seekPositionMs)
                currentPlayer.play()
                isSeeking = false
            },
            onMuteToggle = { videoPlayerPool.toggleMute() },
            onFullscreenClick = { onFullscreenClick(videoUrl, thumbnailUrl, containerBounds) }
        )
    } else {
        VideoPlayerContent(
            currentPlayer = null,
            thumbnailUrl = thumbnailUrl,
            progress = 0f,
            timeText = "",
            isControlVisible = false,
            isMuted = isMuted,
            isVideoReady = false,
            isFullscreen = false,
            onVideoClick = {},
            onPlayVideoClick = { onPlayVideoClick(videoUrl) },
            onContainerPositioned = {},
            onSeekValueChange = {},
            onSeekValueChangeFinished = {},
            onMuteToggle = { videoPlayerPool.toggleMute() },
            onFullscreenClick = {}
        )
    }
}

@Composable
private fun VideoPlayerContent(
    currentPlayer: AutoVideoPlayer?,
    thumbnailUrl: String?,
    progress: Float,
    timeText: String,
    isControlVisible: Boolean,
    isMuted: Boolean,
    isVideoReady: Boolean,
    isFullscreen: Boolean,
    onVideoClick: () -> Unit,
    onPlayVideoClick: () -> Unit,
    onContainerPositioned: (Rect) -> Unit,
    onSeekValueChange: (Float) -> Unit,
    onSeekValueChangeFinished: () -> Unit,
    onMuteToggle: () -> Unit,
    onFullscreenClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val thumbnailAlpha by animateFloatAsState(
        targetValue = if (isVideoReady) 0f else 1f,
        animationSpec = tween(durationMillis = 200),
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .onGloballyPositioned { coordinates ->
                onContainerPositioned(coordinates.boundsInWindow())
            }
            .then(
                if (isVideoReady) Modifier.clickable { onVideoClick() }
                else Modifier
            )
    ) {
        // 오버레이 열려있으면 VideoPlayerView 제거 → 닫힐 때 factory 재실행으로 자동 reattach
//        if (autoPlayer != null && !isFullscreen) {
//            VideoPlayerView(
//                autoPlayer = autoPlayer,
//                modifier = Modifier.fillMaxSize(),
//            )
//        }

        if (currentPlayer != null) {
            if (!isFullscreen) {
                VideoPlayerView(
                    autoPlayer = currentPlayer,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                // PlayerView 빠지는 순간 썸네일로 커버
                if (thumbnailUrl != null) {
                    AsyncImage(
                        model = thumbnailUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                    )
                }
            }
        }

        if (thumbnailUrl != null && thumbnailAlpha > 0f) {
            ThumbnailWrapper(
                thumbnailUrl = thumbnailUrl,
                onPlayVideoClick = onPlayVideoClick,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(thumbnailAlpha),
            )
        }

        if (currentPlayer != null) {
            PlayerControlOverlay(
                progress = progress,
                timeText = timeText,
                isControlVisible = isControlVisible,
                isMuted = isMuted,
                onSeekValueChange = onSeekValueChange,
                onSeekValueChangeFinished = onSeekValueChangeFinished,
                onMuteToggle = onMuteToggle,
                onFullscreenClick = onFullscreenClick,
            )
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerView(
    autoPlayer: AutoVideoPlayer,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        factory = {
            autoPlayer.playerView.apply {
                (parent as? ViewGroup)?.removeView(this)
            }
        },
        modifier = modifier,
    )
}

@Composable
private fun ThumbnailWrapper(
    thumbnailUrl: String?,
    onPlayVideoClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.clickable { onPlayVideoClick() }
    ) {
        AsyncImage(
            model = thumbnailUrl,
            contentDescription = stringResource(R.string.desc_video_thumbnail),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit,
        )
        PlayerThumbnailIcon(modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
private fun GuestBookItemAudioSection(
    audioMedias: ImmutableList<GuestBookMediaUiModel>,
    audioPlaybackState: AudioPlaybackState,
    onAudioMediaClick: (GuestBookMediaUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (audioMedias.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = NachoSpacing.large),
        verticalArrangement = Arrangement.spacedBy(NachoSpacing.small),
    ) {
        audioMedias.forEach { audio ->
            val isCurrentAudio = audio.url == audioPlaybackState.playingUrl

            GuestBookAudioItem(
                audio = audio,
                isAudioPlaying = audioPlaybackState.isAudioPlayingForUrl(audio.url),
                isCurrentAudio = isCurrentAudio,
                isLoading = isCurrentAudio && audioPlaybackState.isLoading,
                currentPositionMs = audioPlaybackState.currentPositionMs,
                totalDurationMs = audioPlaybackState.totalDurationMs,
                onAudioMediaClick = onAudioMediaClick,
            )
        }
    }
}

@Composable
private fun GuestBookAudioItem(
    audio: GuestBookMediaUiModel,
    isAudioPlaying: Boolean,
    isCurrentAudio: Boolean,
    isLoading: Boolean,
    currentPositionMs: Long,
    totalDurationMs: Long,
    onAudioMediaClick: (GuestBookMediaUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val playIconResId = if (isAudioPlaying) {
        R.drawable.ic_pause_filled_24
    } else {
        R.drawable.ic_play_arrow_24
    }

    val originalDurationMs = (audio.durationSeconds?.times(1000))?.toLong() ?: 0L
    val isReady = isCurrentAudio && totalDurationMs > 0

    val remainingDurationMs = if (isReady) {
        (totalDurationMs - currentPositionMs).coerceAtLeast(0L)
    } else {
        originalDurationMs
    }

    val progress = if (isReady) {
        (currentPositionMs.toFloat() / totalDurationMs.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = if (progress == 0f) snap() else tween(durationMillis = 100, easing = LinearEasing)
    )

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(
                    color = NachoTheme.colorScheme.backgroundSurface,
                    shape = NachoTheme.shapes.small,
                )
                .padding(NachoSpacing.large),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(NachoSpacing.small),
    ) {
        Box(
            modifier =
                Modifier
                    .size(NachoIconSize.xLarge)
                    .background(
                        color = NachoTheme.colorScheme.brandPrimary,
                        shape = CircleShape,
                    ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_mic_filled_18),
                contentDescription = stringResource(R.string.desc_audio_media_icon),
                tint = NachoTheme.colorScheme.iconTertiary,
            )
        }
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(NachoTheme.shapes.small),
            color = NachoTheme.colorScheme.brandPrimary,
            trackColor = NachoTheme.colorScheme.backgroundBorder,
            gapSize = 0.dp,
            strokeCap = StrokeCap.Square,
            drawStopIndicator = { /* No-op */ },
        )
        Text(
            text = (remainingDurationMs / 1000).toInt().toFormatDuration(),
            style = NachoTheme.typography.bodySmallRegular,
            color = NachoTheme.colorScheme.textSecondary,
            modifier = Modifier.widthIn(min = 40.dp)
        )
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = NachoTheme.colorScheme.backgroundPrimary,
            border =
                BorderStroke(
                    width = NachoStroke.small,
                    color = NachoTheme.colorScheme.iconDisabled,
                ),
            onClick = { onAudioMediaClick(audio) },
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isCurrentAudio && isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(NachoIconSize.xSmall),
                        color = NachoTheme.colorScheme.textSecondary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        painter = painterResource(playIconResId),
                        contentDescription = stringResource(R.string.desc_play_audio),
                        tint = NachoTheme.colorScheme.textSecondary,
                    )
                }
            }
        }
    }
}

@PreviewTheme
@Composable
private fun GuestBookItemPreview() {
    NachoTheme {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(NachoSpacing.large),
        ) {
            item {
                GuestBookItem(
                    guestBook =
                        GuestBookUiModel(
                            id = 1L,
                            invitation =
                                GuestBookInvitationUiModel(
                                    id = 1001L,
                                    title = "우리 결혼해요!",
                                ),
                            author =
                                AuthorUiModel(
                                    id = 5001L,
                                    name = "홍길동",
                                    profileImageUrl = null,
                                ),
                            textContent = "축하합니다!\n 행복하세요!축하합니다! 행복하세요!축하합니다! 행복하세요!축하합니다! 행복하세요!축하합니다! 행복하세요!축하합니다! 행복하세요!축하",
                            visualMedias =
                                listOf(
                                    GuestBookMediaUiModel(
                                        id = 1L,
                                        type = MediaUiType.IMAGE,
                                        url = "",
                                        thumbnailUrl = "",
                                        durationSeconds = 34,
                                        displayOrder = 0,
                                    ),
                                    GuestBookMediaUiModel(
                                        id = 2L,
                                        type = MediaUiType.VIDEO,
                                        url = "",
                                        thumbnailUrl = "",
                                        durationSeconds = 30,
                                        displayOrder = 1,
                                    ),
                                ).toImmutableList(),
                            audioMedias =
                                listOf(
                                    GuestBookMediaUiModel(
                                        id = 3L,
                                        type = MediaUiType.AUDIO,
                                        url = "",
                                        thumbnailUrl = "",
                                        durationSeconds = 45,
                                        displayOrder = 0,
                                    ),
                                ).toImmutableList(),
                            totalVisualCount = 2,
                            isOwner = true,
                            isInvitationOwner = false,
                            createdAt = LocalDateTime(2025, 6, 1, 12, 0),
                            updatedAt = LocalDateTime(2025, 6, 1, 12, 0),
                        ),
                    videoPlayerPool = FakeAutoVideoPlayerPool(),
                    shouldPlayVideo = false,
                    audioPlaybackState = AudioPlaybackState(),
                    isEditing = true,
                    onInvitationTitleClick = {},
                    onVisualMediaClick = {},
                    onAudioMediaClick = {},
                    onPlayVideoClick = {},
                    onEditClick = {},
                    onDeleteClick = {},
                )
                GuestBookItem(
                    guestBook =
                        GuestBookUiModel(
                            id = 1L,
                            invitation =
                                GuestBookInvitationUiModel(
                                    id = 1001L,
                                    title = "우리 결혼해요!",
                                ),
                            author =
                                AuthorUiModel(
                                    id = 5001L,
                                    name = "홍길동",
                                    profileImageUrl = null,
                                ),
                            textContent = "축하합니다!\n 행복하세요!축하합니다! 행복하세요!축하합니다! 행복하세요!축하합니다! 행복하세요!축하합니다! 행복하세요!축하합니다! 행복하세요!축하",
                            visualMedias =
                                listOf(
                                    GuestBookMediaUiModel(
                                        id = 1L,
                                        type = MediaUiType.IMAGE,
                                        url = "",
                                        thumbnailUrl = "",
                                        durationSeconds = 34,
                                        displayOrder = 0,
                                    ),
                                    GuestBookMediaUiModel(
                                        id = 2L,
                                        type = MediaUiType.VIDEO,
                                        url = "",
                                        thumbnailUrl = "",
                                        durationSeconds = 30,
                                        displayOrder = 1,
                                    ),
                                ).toImmutableList(),
                            audioMedias =
                                listOf(
                                    GuestBookMediaUiModel(
                                        id = 3L,
                                        type = MediaUiType.AUDIO,
                                        url = "",
                                        thumbnailUrl = "",
                                        durationSeconds = 45,
                                        displayOrder = 0,
                                    ),
                                ).toImmutableList(),
                            totalVisualCount = 2,
                            isOwner = true,
                            isInvitationOwner = false,
                            createdAt = LocalDateTime(2025, 6, 1, 12, 0),
                            updatedAt = LocalDateTime(2025, 6, 1, 12, 0),
                        ),
                    videoPlayerPool = FakeAutoVideoPlayerPool(),
                    shouldPlayVideo = false,
                    audioPlaybackState = AudioPlaybackState(),
                    isEditing = false,
                    onInvitationTitleClick = {},
                    onVisualMediaClick = {},
                    onAudioMediaClick = {},
                    onPlayVideoClick = {},
                    onEditClick = {},
                    onDeleteClick = {},
                )
            }
        }
    }
}
