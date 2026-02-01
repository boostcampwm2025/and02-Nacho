package com.andlife.home.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import com.andlife.home.model.home.HomeSideEffect
import com.andlife.home.model.home.HomeUiEvent
import com.andlife.home.model.home.HomeUiState
import com.andlife.home.viewmodel.HomeViewModel
import com.andlife.media.video.AutoVideoPlayer
import com.andlife.media.video.AutoVideoPlayerPool
import com.andlife.model.common.VideoCandidate
import com.andlife.model.guestbook.GuestBookUiModel
import com.andlife.model.guestbook.MediaUiType
import com.andlife.model.invitation.UpcomingInvitationUiModel
import com.andlife.ui.component.guestbook.FakeAutoVideoPlayerPool
import com.andlife.ui.component.guestbook.GuestBookItem
import com.andlife.ui.component.listitem.InvitationScheduleListItem
import com.andlife.ui.component.listitem.InvitationScheduleListItemSkeleton
import com.andlife.ui.component.loading.InvitationLoadingIndicator
import com.andlife.ui.util.collectWithLifecycle
import com.andlife.ui.util.toDDayText
import com.andlife.ui.util.toDateTimeSingleLine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min
import com.andlife.designsystem.R as designR

private const val GUESTBOOK_KEY_OFFSET = 2
private const val UPCOMING_CARD_WIDTH_RATIO = 0.85f
private const val SKELETON_ITEM_COUNT = 2

@Composable
fun HomeRoute(
    snackbarHostState: SnackbarHostState,
    onNavigateToCreate: () -> Unit,
    onNavigateToInvitationDetail: (Long) -> Unit,
    onNavigateToMyInvitationDetail: (Long) -> Unit,
    onNavigateToSetting: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isMediaActive by remember { mutableStateOf(true) }
    val upcomingInvitations = viewModel.upcomingInvitationsPagingFlow.collectAsLazyPagingItems()
    val guestBooks = viewModel.guestBooksPagingFlow.collectAsLazyPagingItems()

    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val lazyListState = rememberLazyListState()
    val refreshFailMessage = stringResource(R.string.snack_refresh_failure)

    viewModel.effectFlow.collectWithLifecycle { effect ->
        when (effect) {
            is HomeSideEffect.ShowMessage -> {
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(effect.message)
                }
            }

            is HomeSideEffect.NavigateToInvitationDetail -> {
                isMediaActive = false
                onNavigateToInvitationDetail(effect.invitationId)
            }

            is HomeSideEffect.NavigateToMyInvitationDetail -> {
                isMediaActive = false
                onNavigateToMyInvitationDetail(effect.invitationId)
            }

            is HomeSideEffect.NavigateToSetting -> {
                isMediaActive = false
                onNavigateToSetting()
            }

            is HomeSideEffect.NavigateToCreate -> {
                isMediaActive = false
                onNavigateToCreate()
            }

            is HomeSideEffect.ScrollToTop -> {
                scope.launch { lazyListState.animateScrollToItem(0) }
            }

            is HomeSideEffect.RefreshFailure -> {
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(refreshFailMessage)
                }
            }

            is HomeSideEffect.NeedRefresh -> {
                upcomingInvitations.refresh()
                guestBooks.refresh()
            }
        }
    }

    LaunchedEffect(upcomingInvitations.loadState.refresh, guestBooks.loadState.refresh) {
        val upcomingState = upcomingInvitations.loadState.refresh
        val guestBookState = guestBooks.loadState.refresh

        if (upcomingState !is LoadState.Loading && guestBookState !is LoadState.Loading) {
            viewModel.onRefreshFinished(
                hasError = upcomingState is LoadState.Error || guestBookState is LoadState.Error
            )
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
                    viewModel.onEvent(HomeUiEvent.UpdateMediaPlayState(true))
                    viewModel.videoPlayerPool.resumeLastPlayed()
                    isMediaActive = true
                }

                Lifecycle.Event.ON_PAUSE -> {
                    viewModel.onEvent(HomeUiEvent.UpdateMediaPlayState(false))
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
        isMediaActive = isMediaActive,
        upcomingInvitations = upcomingInvitations,
        guestBooks = guestBooks,
        onEvent = viewModel::onEvent,
        lazyListState = lazyListState,
        videoPlayerPool = viewModel.videoPlayerPool,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    isMediaActive: Boolean,
    upcomingInvitations: LazyPagingItems<UpcomingInvitationUiModel>,
    guestBooks: LazyPagingItems<GuestBookUiModel>,
    onEvent: (HomeUiEvent) -> Unit,
    lazyListState: LazyListState,
    videoPlayerPool: AutoVideoPlayerPool,
    modifier: Modifier = Modifier,
) {
    var playVideoIndex by remember { mutableStateOf(-1) }

    LaunchedEffect(lazyListState, guestBooks.itemCount, isMediaActive, uiState.audioPlaybackState.isPlaying) {
        var pendingIndex = -1
        var lastChangedTime = 0L
        if (!isMediaActive || uiState.audioPlaybackState.isPlaying) {
            playVideoIndex = -1
            return@LaunchedEffect
        }

        snapshotFlow { lazyListState.layoutInfo }
            .collect { layoutInfo ->
                val visibleItems = layoutInfo.visibleItemsInfo
                if (visibleItems.isEmpty()) return@collect

                val videoCandidates = visibleItems.mapNotNull { itemInfo ->
                    val keyIndex = (itemInfo.key as? Int) ?: return@mapNotNull null
                    val guestBookIndex = keyIndex - GUESTBOOK_KEY_OFFSET
                    if (guestBookIndex < 0 || guestBookIndex >= guestBooks.itemCount) return@mapNotNull null

                    val guestBook = try {
                        guestBooks.peek(guestBookIndex)
                    } catch (e: Exception) {
                        null
                    }

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

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            HomeTopBar(
                title = stringResource(R.string.txt_title_home),
                onClickSetting = { onEvent(HomeUiEvent.ClickSetting) },
            )
        },
        containerColor = NachoTheme.colorScheme.backgroundPrimary,
    ) { paddingValues ->

        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = {
                upcomingInvitations.refresh()
                guestBooks.refresh()
                onEvent(HomeUiEvent.Refresh)
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding()),
        ) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(NachoSpacing.large),
                contentPadding = PaddingValues(
                    bottom = paddingValues.calculateBottomPadding(),
                ),
            ) {
                homeUpcomingSection(
                    upcomingInvitations = upcomingInvitations,
                    onInvitationClick = { id, isOwner ->
                        onEvent(HomeUiEvent.ClickUpcomingInvitation(id, isOwner))
                    },
                    onNavigateToCreate = { onEvent(HomeUiEvent.ClickCreate) },
                )

                homeGuestBookSection(
                    isMediaActive = isMediaActive,
                    guestBooks = guestBooks,
                    uiState = uiState,
                    playVideoIndex = playVideoIndex,
                    videoPlayerPool = videoPlayerPool,
                    onInvitationTitleClick = { id, isOwner ->
                        onEvent(HomeUiEvent.ClickInvitationTitle(id, isOwner))
                    },
                    onVisualMediaClick = { url -> onEvent(HomeUiEvent.ClickVisualMedia(url)) },
                    onAudioMediaClick = { url -> onEvent(HomeUiEvent.ClickAudioMedia(url)) },
                    onPlayVideoClick = { url, itemId ->
                        onEvent(HomeUiEvent.ClickVideoPlayButton(url, itemId))
                    },
                )
            }
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

private fun LazyListScope.homeUpcomingSection(
    upcomingInvitations: LazyPagingItems<UpcomingInvitationUiModel>,
    onInvitationClick: (invitationId: Long, isOwner: Boolean) -> Unit,
    onNavigateToCreate: () -> Unit,
) {
    val refreshState = upcomingInvitations.loadState.refresh
    val isInitialLoading = refreshState is LoadState.Loading && upcomingInvitations.itemCount == 0
    val isInitialError = refreshState is LoadState.Error && upcomingInvitations.itemCount == 0
    val isEmpty = refreshState is LoadState.NotLoading && upcomingInvitations.itemCount == 0

    item {
        Text(
            text = stringResource(R.string.txt_title_upcoming_schedule),
            style = NachoTheme.typography.headingSmallSemiBold,
            modifier = Modifier
                .padding(top = NachoSpacing.medium)
                .padding(horizontal = NachoSpacing.large),
        )
    }

    item {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            contentAlignment = Alignment.Center
        ) {
            if (isInitialLoading || isInitialError || isEmpty) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(NachoSpacing.large),
                    contentPadding = PaddingValues(horizontal = NachoSpacing.large),
                    userScrollEnabled = false
                ) {
                    items(SKELETON_ITEM_COUNT) {
                        InvitationScheduleListItemSkeleton(
                            modifier = Modifier
                                .fillParentMaxWidth(UPCOMING_CARD_WIDTH_RATIO)
                                .alpha(0.5f),
                        )
                    }
                }
            }

            when {
                isInitialError -> {
                    UpcomingStatusContent(
                        title = stringResource(R.string.txt_error_upcoming_title),
                        description = stringResource(R.string.txt_error_upcoming_desc),
                    )
                }

                isEmpty -> {
                    UpcomingStatusContent(
                        title = stringResource(R.string.txt_empty_upcoming_title),
                        description = stringResource(R.string.txt_empty_upcoming_desc),
                        buttonText = stringResource(R.string.txt_action_create_invitation),
                        onButtonClick = onNavigateToCreate,
                        buttonIconRes = designR.drawable.ic_add_24
                    )
                }

                isInitialLoading -> {
                    InvitationLoadingIndicator()
                }

                else -> {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(NachoSpacing.large),
                        contentPadding = PaddingValues(horizontal = NachoSpacing.large)
                    ) {
                        items(
                            count = upcomingInvitations.itemCount,
                            key = upcomingInvitations.itemKey { it.id }
                        ) { index ->
                            upcomingInvitations[index]?.let { invitation ->
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
                                    onClick = { onInvitationClick(invitation.id, invitation.isOwner) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UpcomingStatusContent(
    title: String,
    description: String,
    buttonText: String? = null,
    onButtonClick: (() -> Unit)? = null,
    buttonIconRes: Int? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = NachoSpacing.twoXLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(NachoSpacing.medium)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(NachoSpacing.small)
        ) {
            Text(
                text = title,
                style = NachoTheme.typography.headingSmallSemiBold,
                color = NachoTheme.colorScheme.textPrimary,
                textAlign = TextAlign.Center
            )
            Text(
                text = description,
                style = NachoTheme.typography.bodyMediumRegular,
                color = NachoTheme.colorScheme.textSecondary,
                textAlign = TextAlign.Center
            )
        }

        if (buttonText != null && onButtonClick != null) {
            NachoButton(
                onClick = onButtonClick,
                modifier = Modifier.padding(top = NachoSpacing.small)
            ) {
                buttonIconRes?.let {
                    Icon(painter = painterResource(it), contentDescription = null)
                    Spacer(Modifier.width(NachoSpacing.small))
                }
                Text(text = buttonText)
            }
        }
    }
}

private fun LazyListScope.homeGuestBookSection(
    isMediaActive: Boolean,
    guestBooks: LazyPagingItems<GuestBookUiModel>,
    uiState: HomeUiState,
    playVideoIndex: Int,
    videoPlayerPool: AutoVideoPlayerPool,
    onInvitationTitleClick: (invitationId: Long, isOwner: Boolean) -> Unit,
    onVisualMediaClick: (String) -> Unit,
    onAudioMediaClick: (String) -> Unit,
    onPlayVideoClick: (String, Long) -> Unit,
) {
    item {
        Text(
            text = stringResource(R.string.txt_title_new_post),
            style = NachoTheme.typography.headingSmallSemiBold,
            modifier = Modifier
                .padding(top = NachoSpacing.medium)
                .padding(horizontal = NachoSpacing.large),
        )
    }


    val refreshState = guestBooks.loadState.refresh
    val isInitialLoading = refreshState is LoadState.Loading && guestBooks.itemCount == 0
    val isInitialError = refreshState is LoadState.Error && guestBooks.itemCount == 0
    val isEmpty = refreshState is LoadState.NotLoading && guestBooks.itemCount == 0

    if (isInitialLoading || isInitialError || isEmpty) {
        item {
            GuestBookStatusContent(
                modifier = Modifier.padding(
                    vertical = NachoSpacing.threeXLarge,
                    horizontal = NachoSpacing.large
                ),
                isLoading = isInitialLoading,
                title = when {
                    isInitialError -> stringResource(R.string.error_msg_failed_load_post)
                    isEmpty -> stringResource(R.string.txt_empty_new_post_desc)
                    else -> null
                },
            )
        }
    } else {
        items(
            count = guestBooks.itemCount,
            key = { index -> index + GUESTBOOK_KEY_OFFSET },
        ) { index ->
            guestBooks[index]?.let { guestBook ->
                GuestBookItem(
                    modifier = Modifier.animateItem(),
                    guestBook = guestBook,
                    useMenuButton = false,
                    videoPlayerPool = videoPlayerPool,
                    shouldPlayVideo = uiState.canPlayVideo && (index == playVideoIndex),
                    audioPlaybackState = uiState.audioPlaybackState,
                    onInvitationTitleClick = {
                        onInvitationTitleClick(
                            guestBook.invitation?.id ?: -1L,
                            guestBook.isOwner,
                        )
                    },
                    onVisualMediaClick = { onVisualMediaClick(it.url) },
                    onAudioMediaClick = { onAudioMediaClick(it.url) },
                    onMenuClick = { },
                    onPlayVideoClick = { url -> onPlayVideoClick(url, guestBook.id) },
                )
            }
        }
    }

    if (guestBooks.loadState.append is LoadState.Loading) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(NachoSpacing.large),
                contentAlignment = Alignment.Center
            ) {
                InvitationLoadingIndicator()
            }
        }
    }
}

@Composable
private fun GuestBookStatusContent(
    modifier: Modifier = Modifier,
    title: String? = null,
    description: String? = null,
    buttonText: String? = null,
    onButtonClick: (() -> Unit)? = null,
    isLoading: Boolean = false,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = NachoSpacing.threeXLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        if (isLoading) {
            InvitationLoadingIndicator()
        } else {
            title?.let {
                Text(
                    text = it,
                    style = NachoTheme.typography.bodyMediumRegular,
                    color = NachoTheme.colorScheme.textPrimary,
                    textAlign = TextAlign.Center
                )
            }

            description?.let {
                Spacer(Modifier.height(NachoSpacing.small))
                Text(
                    text = it,
                    style = NachoTheme.typography.bodyMediumRegular,
                    color = NachoTheme.colorScheme.textSecondary,
                    textAlign = TextAlign.Center
                )
            }

            if (buttonText != null && onButtonClick != null) {
                Spacer(Modifier.height(NachoSpacing.large))
                NachoButton(onClick = onButtonClick) {
                    Text(text = buttonText)
                }
            }
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
            override fun clearCacheById(itemId: Long?) {}
            override fun resetPool() {}
            override fun releaseAllPlayers() {}
        }
    }
    val emptyUpcomingInvitations = flowOf(PagingData.empty<UpcomingInvitationUiModel>()).collectAsLazyPagingItems()
    val emptyGuestBooks = flowOf(PagingData.empty<GuestBookUiModel>()).collectAsLazyPagingItems()
    val lazyListState = rememberLazyListState()

    NachoTheme {
        HomeScreen(
            uiState = HomeUiState(),
            isMediaActive = false,
            upcomingInvitations = emptyUpcomingInvitations,
            guestBooks = emptyGuestBooks,
            onEvent = {},
            videoPlayerPool = FakeAutoVideoPlayerPool(),
            lazyListState = lazyListState,
        )
    }
}
