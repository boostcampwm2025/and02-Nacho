package com.andlife.ui.component.guestbook

import androidx.annotation.OptIn
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoStroke
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.media.video.AutoVideoPlayerPool
import com.andlife.media.video.AutoVideoPlayerPoolImpl
import com.andlife.model.common.AuthorUiModel
import com.andlife.model.guestbook.GuestBookInvitationUiModel
import com.andlife.model.guestbook.GuestBookMediaUiModel
import com.andlife.model.guestbook.GuestBookUiModel
import com.andlife.model.guestbook.MediaUiType
import com.andlife.ui.R
import com.andlife.ui.component.media.MediaOverlay
import com.andlife.ui.util.toFormatDuration
import com.andlife.ui.util.toRelativeTimeString
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.LocalDateTime
import com.andlife.designsystem.R as designR

@Composable
fun GuestBookItem(
    guestBook: GuestBookUiModel,
    videoPlayerPool: AutoVideoPlayerPool,
    onInvitationTitleClick: (Long) -> Unit,
    onVisualMediaClick: (GuestBookMediaUiModel) -> Unit,
    onAudioMediaClick: (GuestBookMediaUiModel) -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
    shouldPlayVideo: Boolean = false,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(NachoSpacing.medium),
    ) {
        GuestBookItemHeader(
            author = guestBook.author,
            createdAt = guestBook.createdAt,
            isOwner = guestBook.isOwner,
            onMenuClick = onMenuClick,
        )
        GuestBookItemTextSection(
            invitation = guestBook.invitation,
            textContent = guestBook.textContent,
            onInvitationTitleClick = onInvitationTitleClick,
        )
        if (guestBook.visualMedias.isNotEmpty()) {
            GuestBookItemVisualMediaSection(
                guestBookId = guestBook.id,
                visualMediaUrls = guestBook.visualMedias,
                totalVisualCount = guestBook.totalVisualCount,
                shouldPlayVideo = shouldPlayVideo,
                videoPlayerPool = videoPlayerPool,
                onVisualMediaClick = onVisualMediaClick,
            )
        }
        if (guestBook.audioMedias.isNotEmpty()) {
            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.spacedBy(NachoSpacing.small),
            ) {
                guestBook.audioMedias.forEach { audio ->
                    GuestBookAudioItem(
                        audio = audio,
                        onAudioMediaClick = onAudioMediaClick,
                    )
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(top = NachoSpacing.xSmall),
            color = NachoTheme.colorScheme.backgroundBorder,
        )
    }
}

@Composable
private fun GuestBookItemHeader(
    author: AuthorUiModel,
    createdAt: LocalDateTime,
    isOwner: Boolean,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
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
        if (isOwner) {
            IconButton(onClick = onMenuClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_more_vert_24),
                    contentDescription = stringResource(R.string.desc_edit_guest_book),
                    tint = NachoTheme.colorScheme.textPrimary,
                )
            }
        }
    }
}

@Composable
private fun GuestBookItemTextSection(
    invitation: GuestBookInvitationUiModel?,
    textContent: String,
    onInvitationTitleClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }
    var isOverflowed by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(NachoSpacing.medium),
    ) {
        invitation?.let {
            Row(
                modifier =
                    Modifier
                        .clickable { onInvitationTitleClick(invitation.id) }
                        .padding(vertical = NachoSpacing.xSmall),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(NachoSpacing.xSmall),
            ) {
                Text(
                    text = invitation.title,
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
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (isOverflowed) {
                            isExpanded = !isExpanded
                        }
                    },
            verticalArrangement = Arrangement.spacedBy(NachoSpacing.small),
        ) {
            Text(
                text = textContent,
                style = NachoTheme.typography.bodyMediumRegular,
                color = NachoTheme.colorScheme.textPrimary,
                maxLines = if (isExpanded) Int.MAX_VALUE else 2,
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
    videoPlayerPool: AutoVideoPlayerPool,
    onVisualMediaClick: (GuestBookMediaUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(pageCount = { visualMediaUrls.size })

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .aspectRatio(1f) // TODO: 추후 미디어 비율에 맞게 조정 필요, 일단 정사각형으로 고정
                .clip(NachoTheme.shapes.small),
    ) {
        HorizontalPager(state = pagerState) { page ->
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
                            shouldPlay = shouldPlayVideo && pagerState.currentPage == page,
                            videoPlayerPool = videoPlayerPool,
                        )

                        media.durationSeconds?.let {
                            MediaOverlay(
                                modifier =
                                    Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(NachoSpacing.small),
                                text = it.toFormatDuration(), // TODO: 타이머 기능 추가해야 함.
                            )
                        }
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
    shouldPlay: Boolean,
    videoPlayerPool: AutoVideoPlayerPool,
    modifier: Modifier = Modifier,
) {
    var isVideoReady by remember(videoUrl, shouldPlay) { mutableStateOf(false) }

    val thumbnailAlpha by animateFloatAsState(
        targetValue = if (isVideoReady) 0f else 1f,
        animationSpec = tween(durationMillis = 200),
    )

    LaunchedEffect(shouldPlay) {
        if (shouldPlay) {
            videoPlayerPool.playPlayer(videoUrl, guestBookId)
        } else {
            videoPlayerPool.pausePlayer(videoUrl)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (shouldPlay) {
            val currentPlayer = videoPlayerPool.getPlayer(videoUrl)

            DisposableEffect(currentPlayer) {
                val listener = object : Player.Listener {
                    override fun onRenderedFirstFrame() {
                        isVideoReady = true
                    }
                }
                currentPlayer.exoPlayer.addListener(listener)
                onDispose { currentPlayer.exoPlayer.removeListener(listener) }
            }

            AndroidView(
                factory = { context ->
                    PlayerView(context).apply {
                        useController = false
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                        player = currentPlayer.exoPlayer
                    }
                },
                modifier = Modifier.fillMaxSize(),
            )
        }
        thumbnailUrl?.let {
            ThumbnailWrapper(
                thumbnailUrl = it,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(thumbnailAlpha),
            )
        }
    }
}

@Composable
private fun ThumbnailWrapper(
    thumbnailUrl: String?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
    ) {
        AsyncImage(
            model = thumbnailUrl,
            contentDescription = stringResource(R.string.desc_video_thumbnail),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit,
        )
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(48.dp)
                .background(
                    color = NachoTheme.colorScheme.iconSecondary.copy(alpha = 0.6f),
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_play_arrow_24),
                contentDescription = stringResource(R.string.desc_play_video),
                tint = NachoTheme.colorScheme.iconTertiary,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Composable
private fun GuestBookAudioItem(
    audio: GuestBookMediaUiModel,
    onAudioMediaClick: (GuestBookMediaUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(
                    color = NachoTheme.colorScheme.brandLight,
                    shape = NachoTheme.shapes.small,
                )
                .padding(NachoSpacing.large),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(NachoSpacing.small),
    ) {
        Box(
            modifier =
                Modifier
                    .size(48.dp)
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
        Column(
            modifier =
                Modifier.weight(1f),
        ) {
            Text(
                text = "오디오 제목", // TODO: 오디오 제목 필요
                style = NachoTheme.typography.bodyMediumMedium,
                color = NachoTheme.colorScheme.textPrimary,
            )
            Text(
                text =
                    stringResource(
                        R.string.format_audio_duration,
                        audio.durationSeconds ?: 0,
                    ),
                style = NachoTheme.typography.bodySmallRegular,
                color = NachoTheme.colorScheme.textSecondary,
            )
        }
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
                Icon(
                    painter = painterResource(R.drawable.ic_play_arrow_24),
                    contentDescription = stringResource(R.string.desc_play_audio),
                    tint = NachoTheme.colorScheme.textSecondary,
                )
            }
        }
    }
}

@PreviewTheme
@Composable
private fun GuestBookItemPreview() {
    NachoTheme {
        LazyColumn(
            modifier =
                Modifier
                    .padding(NachoSpacing.large),
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
                            createdAt = LocalDateTime(2025, 6, 1, 12, 0),
                            updatedAt = LocalDateTime(2025, 6, 1, 12, 0),
                        ),
                    videoPlayerPool = AutoVideoPlayerPoolImpl(LocalContext.current, CacheDataSource.Factory()),
                    shouldPlayVideo = false,
                    onInvitationTitleClick = {},
                    onVisualMediaClick = {},
                    onAudioMediaClick = {},
                    onMenuClick = {},
                )
            }
        }
    }
}
