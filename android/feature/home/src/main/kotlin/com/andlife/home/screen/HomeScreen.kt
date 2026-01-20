package com.andlife.home.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.andlife.designsystem.component.NachoButton
import com.andlife.designsystem.component.NachoDivider
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.home.R
import com.andlife.home.model.HomeSideEffect
import com.andlife.home.model.HomeUiEvent
import com.andlife.home.model.HomeUiState
import com.andlife.home.viewmodel.HomeViewModel
import com.andlife.media.video.AutoVideoPlayer
import com.andlife.media.video.AutoVideoPlayerPool
import com.andlife.model.common.VideoCandidate
import com.andlife.model.guestbook.GuestBookUiModel
import com.andlife.model.guestbook.MediaUiType
import com.andlife.ui.component.guestbook.GuestBookItem
import com.andlife.ui.component.listitem.InvitationScheduleListItem
import com.andlife.ui.component.loading.InvitationLoadingError
import com.andlife.ui.component.loading.InvitationLoadingIndicator
import com.andlife.ui.util.collectWithLifecycle
import com.andlife.ui.util.toDDayText
import com.andlife.ui.util.toDateTimeSingleLine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

private const val GUESTBOOK_KEY_PREFIX = "guestbook_"
private const val GUESTBOOK_EMPTY_HEIGHT_RATIO = 0.3f
private const val GUESTBOOK_LOADING_HEIGHT_RATIO = 0.5f
private const val UPCOMING_CARD_WIDTH_RATIO = 0.85f

@Composable
fun HomeRoute(
    onNavigateToInvitationDetail: (Long) -> Unit,
    onNavigateToSetting: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val guestBooks = viewModel.guestBooksPagingFlow.collectAsLazyPagingItems()
    val snackbarHostState = remember { SnackbarHostState() }
    val lifecycleOwner = LocalLifecycleOwner.current

    viewModel.effectFlow.collectWithLifecycle { effect ->
        when (effect) {
            is HomeSideEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
            is HomeSideEffect.NavigateToInvitationDetail -> onNavigateToInvitationDetail(effect.invitationId)
            is HomeSideEffect.NavigateToSetting -> onNavigateToSetting()
        }
    }

    DisposableEffect(Unit) {
        viewModel.videoPlayerPool.preparePlayers()
        onDispose {
            viewModel.videoPlayerPool.releaseAllPlayers()
            viewModel.audioPlayerManager.release()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.videoPlayerPool.resumeLastPlayed()
                }

                Lifecycle.Event.ON_PAUSE -> {
                    viewModel.videoPlayerPool.pauseAllPlayers()
                    viewModel.audioPlayerManager.pause()
                }

                Lifecycle.Event.ON_DESTROY -> {
                    viewModel.videoPlayerPool.resetPool()
                    viewModel.audioPlayerManager.stopAll()
                }

                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    HomeScreen(
        uiState = uiState,
        guestBooks = guestBooks,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState,
        videoPlayerPool = viewModel.videoPlayerPool,
        modifier = modifier,
    )
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    guestBooks: LazyPagingItems<GuestBookUiModel>,
    onEvent: (HomeUiEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    videoPlayerPool: AutoVideoPlayerPool,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()
    var playVideoIndex by remember { mutableStateOf(-1) }

    LaunchedEffect(lazyListState, guestBooks.itemCount, uiState.isAudioPlaying) {
        var pendingIndex = -1
        var lastChangedTime = 0L
        if (uiState.isAudioPlaying) {
            playVideoIndex = -1
            return@LaunchedEffect
        }

        snapshotFlow { lazyListState.layoutInfo }
            .collect { layoutInfo ->
                val visibleItems = layoutInfo.visibleItemsInfo
                if (visibleItems.isEmpty()) return@collect

                val videoCandidates = visibleItems.mapNotNull { itemInfo ->
                    val itemKey = itemInfo.key.toString()
                    if (!itemKey.startsWith("guestbook_")) return@mapNotNull null
                    val guestBookId = itemKey.removePrefix("guestbook_").toLongOrNull() ?: return@mapNotNull null

                    val guestBookIndex = (0 until guestBooks.itemCount).find {
                        guestBooks.peek(it)?.id == guestBookId
                    } ?: return@mapNotNull null

                    val guestBook = guestBooks.peek(guestBookIndex)
                    val hasVideo = guestBook?.visualMedias?.any { it.type == MediaUiType.VIDEO } == true
                    if (!hasVideo) return@mapNotNull null

                    val visibleHeight = min(itemInfo.offset + itemInfo.size, layoutInfo.viewportEndOffset) -
                        max(itemInfo.offset, layoutInfo.viewportStartOffset)
                    val visibilityRatio = visibleHeight.toFloat() / itemInfo.size

                    VideoCandidate(guestBookIndex, visibilityRatio)
                }

                if (videoCandidates.isEmpty()) {
                    playVideoIndex = -1
                    return@collect
                }

                val (bestIndex, bestVisibilityRatio) = videoCandidates.maxBy { it.visibilityRatio }
                val currentPlayingItem = videoCandidates.find { it.index == playVideoIndex }
                val currentPlayingRatio = currentPlayingItem?.visibilityRatio ?: 0f

                val shouldChangeTo = when {
                    playVideoIndex != -1 && currentPlayingRatio < 0.2f -> -1
                    playVideoIndex == -1 -> if (bestVisibilityRatio >= 0.6f) bestIndex else -1
                    bestIndex != playVideoIndex && bestVisibilityRatio > currentPlayingRatio + 0.3f -> bestIndex
                    else -> playVideoIndex
                }

                if (shouldChangeTo == -1 && playVideoIndex != -1) {
                    playVideoIndex = -1
                    pendingIndex = -1
                } else if (shouldChangeTo != playVideoIndex && shouldChangeTo != pendingIndex) {
                    pendingIndex = shouldChangeTo
                    lastChangedTime = System.currentTimeMillis()
                    launch {
                        delay(200L)
                        if (pendingIndex == shouldChangeTo && System.currentTimeMillis() - lastChangedTime >= 200L) {
                            playVideoIndex = shouldChangeTo
                            pendingIndex = -1
                        }
                    }
                }
            }
    }

    LaunchedEffect(guestBooks.loadState.refresh) {
        if (guestBooks.loadState.refresh is LoadState.NotLoading) {
            if (guestBooks.itemCount > 0) {
                lazyListState.animateScrollToItem(0)
            }
            playVideoIndex = -1
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        topBar = {
            HomeTopBar(
                title = stringResource(R.string.txt_title_home),
                onClickSetting = { onEvent(HomeUiEvent.ClickSetting) },
            )
        },
        containerColor = NachoTheme.colorScheme.backgroundPrimary,
    ) { paddingValues ->
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(NachoSpacing.large),
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding(),
                start = NachoSpacing.large,
                end = NachoSpacing.large,
            ),
        ) {
            item {
                HomeUpcomingSection(
                    uiState = uiState,
                    onInvitationClick = { id -> onEvent(HomeUiEvent.ClickUpcomingInvitation(id)) },
                    onRetryClick = { onEvent(HomeUiEvent.RetryLoad) },
                    onCreateClick = { /* TODO: 초대장 생성 화면 이동 */ }
                )
            }

            homeGuestBookSection(
                guestBooks = guestBooks,
                uiState = uiState,
                playVideoIndex = playVideoIndex,
                videoPlayerPool = videoPlayerPool,
                onInvitationTitleClick = { id -> onEvent(HomeUiEvent.ClickInvitationTitle(id)) },
                onVisualMediaClick = { url -> onEvent(HomeUiEvent.ClickVisualMedia(url)) },
                onAudioMediaClick = { url -> onEvent(HomeUiEvent.ClickAudioMedia(url)) },
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(
    title: String,
    onClickSetting: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        TopAppBar(
            modifier = Modifier.fillMaxWidth(),
            title = {
                Text(
                    text = title,
                    style = NachoTheme.typography.headingSmallSemiBold,
                    color = NachoTheme.colorScheme.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            actions = {
                IconButton(onClick = onClickSetting) {
                    Icon(
                        painter = painterResource(R.drawable.ic_setting_24),
                        contentDescription = stringResource(R.string.desc_top_bar_setting),
                        tint = Color.Unspecified,
                    )
                }
            },
            windowInsets = WindowInsets(),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = NachoTheme.colorScheme.backgroundPrimary,
            ),
        )

        NachoDivider(
            color = NachoTheme.colorScheme.backgroundSecondary,
            horizontalPadding = NachoSpacing.none,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun HomeUpcomingSection(
    uiState: HomeUiState,
    onInvitationClick: (Long) -> Unit,
    onRetryClick: () -> Unit,
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(NachoSpacing.large)
    ) {
        Text(
            text = stringResource(R.string.txt_title_upcoming_schedule),
            style = NachoTheme.typography.headingSmallSemiBold,
            modifier = Modifier.padding(top = NachoSpacing.large),
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 240.dp)
                .wrapContentHeight(),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isUpcomingLoading -> {
                    InvitationLoadingIndicator(
                        modifier = Modifier.fillMaxSize()
                    )
                }

                uiState.isUpcomingError -> {
                    InvitationLoadingError(
                        modifier = Modifier.fillMaxSize(),
                        onRetry = onRetryClick
                    )
                }

                uiState.upcomingInvitations.isEmpty() -> {
                    EmptyUpcomingCard(
                        onWriteClick = onCreateClick,
                        modifier = Modifier.padding(vertical = NachoSpacing.large),
                    )
                }

                else -> {
                    LazyRow(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(NachoSpacing.medium),
                        contentPadding = PaddingValues(bottom = NachoSpacing.small)
                    ) {
                        items(
                            items = uiState.upcomingInvitations,
                            key = { it.id },
                        ) { invitation ->
                            val dDayText = remember(invitation.startTime.date) {
                                invitation.startTime.date.toDDayText()
                            }
                            InvitationScheduleListItem(
                                modifier = Modifier.fillParentMaxWidth(UPCOMING_CARD_WIDTH_RATIO),
                                imageUrl = invitation.thumbnailUrl,
                                title = invitation.title,
                                startTime = invitation.startTime.toDateTimeSingleLine(),
                                hostName = invitation.hostInfo.name,
                                dDayText = dDayText,
                                onClick = { onInvitationClick(invitation.id) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyUpcomingCard(
    onWriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = NachoTheme.colorScheme.backgroundSecondary.copy(alpha = 0.5f)
        ),
        shape = NachoTheme.shapes.medium,
        border = BorderStroke(1.dp, NachoTheme.colorScheme.backgroundBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = NachoSpacing.threeXLarge, horizontal = NachoSpacing.large),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(NachoSpacing.twoXLarge)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(NachoSpacing.medium)
            ) {
                Text(
                    text = stringResource(R.string.txt_empty_upcoming_title),
                    style = NachoTheme.typography.headingSmallSemiBold,
                    color = NachoTheme.colorScheme.textPrimary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.txt_empty_upcoming_desc),
                    style = NachoTheme.typography.bodyLargeRegular,
                    color = NachoTheme.colorScheme.textSecondary,
                    textAlign = TextAlign.Center
                )
            }

            NachoButton(
                onClick = onWriteClick,
                containerColor = NachoTheme.colorScheme.brandOnPrimary,
                contentColor = NachoTheme.colorScheme.brandPrimary,
                modifier = Modifier.padding(top = NachoSpacing.medium)
            ) {
                Icon(
                    painter = painterResource(com.andlife.ui.R.drawable.ic_add_24),
                    contentDescription = null,
                )
                Spacer(Modifier.width(NachoSpacing.small))
                Text(
                    text = stringResource(R.string.txt_action_create_invitation),
                    style = NachoTheme.typography.bodyLargeSemiBold,
                    modifier = Modifier.padding(vertical = NachoSpacing.small)
                )
            }
        }
    }
}
fun LazyListScope.homeGuestBookSection(
    guestBooks: LazyPagingItems<GuestBookUiModel>,
    uiState: HomeUiState,
    playVideoIndex: Int,
    videoPlayerPool: AutoVideoPlayerPool,
    onInvitationTitleClick: (Long) -> Unit,
    onVisualMediaClick: (String) -> Unit,
    onAudioMediaClick: (String) -> Unit,
) {
    item {
        Text(
            text = stringResource(R.string.txt_title_new_post),
            style = NachoTheme.typography.headingSmallSemiBold,
            modifier = Modifier.padding(top = NachoSpacing.medium),
        )
    }

    val refreshState = guestBooks.loadState.refresh
    when (refreshState) {
        is LoadState.Loading -> {
            item {
                InvitationLoadingIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillParentMaxHeight(GUESTBOOK_LOADING_HEIGHT_RATIO),
                )
            }
        }

        is LoadState.Error -> {
            item {
                InvitationLoadingError(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(NachoSpacing.large),
                    onRetry = { guestBooks.retry() },
                )
            }
        }

        is LoadState.NotLoading if guestBooks.itemCount == 0 -> {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillParentMaxHeight(GUESTBOOK_EMPTY_HEIGHT_RATIO),             horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = stringResource(R.string.txt_empty_new_post_desc),
                        style = NachoTheme.typography.bodyLargeRegular,
                        color = NachoTheme.colorScheme.textSecondary,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }

        else -> {
            items(
                count = guestBooks.itemCount,
                key = { index ->
                    val id = guestBooks.itemKey { it.id }.invoke(index)
                    "$GUESTBOOK_KEY_PREFIX$id"
                },
            ) { index ->
                guestBooks[index]?.let { guestBook ->
                    GuestBookItem(
                        modifier = Modifier.animateItem(),
                        guestBook = guestBook,
                        videoPlayerPool = videoPlayerPool,
                        shouldPlayVideo = (index == playVideoIndex),
                        isAudioPlaying = uiState.isAudioPlaying &&
                            guestBook.audioMedias.any { it.url == uiState.playingAudioUrl },
                        onInvitationTitleClick = { onInvitationTitleClick(guestBook.invitation?.id ?: -1L) },
                        playingAudioUrl = uiState.playingAudioUrl,
                        onVisualMediaClick = { onVisualMediaClick(it.url) },
                        onAudioMediaClick = { onAudioMediaClick(it.url) },
                        onMenuClick = { },
                    )
                }
            }
        }
    }

    if (guestBooks.loadState.append is LoadState.Loading) {
        item {
            InvitationLoadingIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = NachoSpacing.large),
            )
        }
    }
}
@PreviewTheme
@Composable
private fun HomeScreenPreview() {
    val fakeVideoPlayerPool = remember {
        object : AutoVideoPlayerPool {
            override fun preparePlayers() {}
            override fun getPlayer(url: String): AutoVideoPlayer {
                throw UnsupportedOperationException("Preview 전용")
            }
            override fun playPlayer(url: String, itemId: Long) {}
            override fun pausePlayer(url: String) {}
            override fun pauseAllPlayers() {}
            override fun resumeLastPlayed() {}
            override fun resetPool() {}
            override fun releaseAllPlayers() {}
        }
    }
    val emptyGuestBooks = flowOf(PagingData.empty<GuestBookUiModel>()).collectAsLazyPagingItems()

    NachoTheme {
        HomeScreen(
            uiState = HomeUiState(),
            guestBooks = emptyGuestBooks,
            onEvent = {},
            snackbarHostState = SnackbarHostState(),
            videoPlayerPool = fakeVideoPlayerPool,
        )
    }
}
