package com.andlife.home.screen

import android.app.Activity
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
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
import com.andlife.media.video.AutoVideoPlayerPool
import com.andlife.media.video.FakeAutoVideoPlayerPool
import com.andlife.model.common.VideoCandidate
import com.andlife.model.guestbook.GuestBookUiModel
import com.andlife.model.guestbook.MediaUiType
import com.andlife.model.invitation.UpcomingInvitationUiModel
import com.andlife.ui.component.dialog.LoginDialog
import com.andlife.ui.component.guestbook.GuestBookItem
import com.andlife.ui.component.listitem.InvitationScheduleListItem
import com.andlife.ui.component.listitem.InvitationScheduleListItemSkeleton
import com.andlife.ui.component.loading.InvitationLoadingIndicator
import com.andlife.ui.component.media.video.FullscreenVideoPlayerContainer
import com.andlife.ui.component.report.ReportBottomSheet
import com.andlife.ui.util.collectWithLifecycle
import com.andlife.ui.util.toDDayText
import com.andlife.ui.util.toDateTimeSingleLine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min
import com.andlife.designsystem.R as designR
import com.andlife.ui.R as uiR

private const val GUESTBOOK_KEY_OFFSET = 2
private const val UPCOMING_CARD_WIDTH_RATIO = 0.85f
private const val SKELETON_ITEM_COUNT = 2

@Composable
fun HomeRoute(
    snackbarHostState: SnackbarHostState,
    onNavigateToCreate: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToInvitationDetail: (Long) -> Unit,
    onNavigateToMyInvitationDetail: (Long) -> Unit,
    onNavigateToSetting: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isMediaActive by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val upcomingInvitations = viewModel.upcomingInvitationsPagingFlow.collectAsLazyPagingItems()
    val guestBooks = viewModel.guestBooksPagingFlow.collectAsLazyPagingItems()
    var lastPrecachedCount by remember { mutableIntStateOf(0) }
    var pendingScrollToTop by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val lazyListState = rememberLazyListState()
    val refreshFailMessage = stringResource(R.string.snack_refresh_failure)
    val reportSuccessMessage = stringResource(uiR.string.msg_report_success)
    val reportFailureMessage = stringResource(uiR.string.msg_report_failure)
    val fullscreenActivityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        viewModel.onEvent(HomeUiEvent.DismissFullscreenVideo)
    }

    val navigateToLoginWithCleanup: () -> Unit = {
        isMediaActive = false
        scope.launch {
            viewModel.videoPlayerPool.pauseAllPlayers()
            viewModel.onEvent(HomeUiEvent.ClickAudioMedia(""))

            delay(50L)
            onNavigateToLogin()
        }
    }

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

            is HomeSideEffect.NeedRefresh -> {
                pendingScrollToTop = true
                upcomingInvitations.refresh()
                guestBooks.refresh()
            }

            HomeSideEffect.ReportSuccess -> {
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(reportSuccessMessage)
                }
            }

            is HomeSideEffect.ReportFailure -> {
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(effect.message ?: reportFailureMessage)
                }
            }
        }
    }

    LaunchedEffect(upcomingInvitations.loadState.mediator?.refresh, guestBooks.loadState.mediator?.refresh) {
        val upcomingState = upcomingInvitations.loadState.mediator?.refresh
        val guestBookState = guestBooks.loadState.mediator?.refresh

        val hasError = upcomingState is LoadState.Error || guestBookState is LoadState.Error
        if (hasError) {
            scope.launch {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(refreshFailMessage)
            }
        }

        val isNotLoading = upcomingState is LoadState.NotLoading && guestBookState is LoadState.NotLoading
        if (isNotLoading && pendingScrollToTop) {
            pendingScrollToTop = false
            lazyListState.animateScrollToItem(0)
        }
    }

    LaunchedEffect(guestBooks.itemCount) {
        val currentCount = guestBooks.itemCount
        if (currentCount < lastPrecachedCount) lastPrecachedCount = 0
        if (currentCount <= lastPrecachedCount) return@LaunchedEffect

        val videoUrls = (lastPrecachedCount until currentCount).mapNotNull { index ->
            val item = guestBooks.peek(index)
            item?.visualMedias?.firstOrNull { it.type == MediaUiType.VIDEO }?.url
        }.distinct()

        lastPrecachedCount = currentCount

        if (videoUrls.isNotEmpty()) {
            viewModel.videoPlayerPool.preparePlayers(videoUrls.size)
            viewModel.videoPlayerPool.precacheVideos(videoUrls)
        }
    }

    DisposableEffect(Unit) {
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
                    if (uiState.fullscreenVideoUrl == null) {
                        viewModel.videoPlayerPool.pauseAllPlayers()
                    }
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

    if (uiState.reportTargetId != null) {
        ReportBottomSheet(
            onSubmit = { reason, description ->
                viewModel.onEvent(HomeUiEvent.SubmitReport(reason, description))
            },
            onDismiss = {
                viewModel.onEvent(HomeUiEvent.DismissReport)
            }
        )
    }

    DisposableEffect(uiState.fullscreenVideoUrl) {
        val videoUrl = uiState.fullscreenVideoUrl ?: return@DisposableEffect onDispose {}

        val activity = context as Activity
        val decorView = activity.window.decorView as ViewGroup

        WindowInsetsControllerCompat(activity.window, decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        val composeView = ComposeView(activity).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
            setContent {
                val player = remember { viewModel.videoPlayerPool.getPlayer(videoUrl) }
                val isMuted by viewModel.videoPlayerPool.isMuted.collectAsStateWithLifecycle()

                FullscreenVideoPlayerContainer(
                    player = player,
                    thumbnailUrl = uiState.fullscreenThumbnailUrl,
                    startBounds = uiState.fullscreenStartBounds,
                    isMuted = isMuted,
                    onDismiss = { viewModel.onEvent(HomeUiEvent.DismissFullscreenVideo) },
                    onMuteToggle = { viewModel.onEvent(HomeUiEvent.ToggleVideoMute) }
                )
            }
        }

        decorView.addView(
            composeView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
        )

        onDispose {
            decorView.removeView(composeView)
            WindowInsetsControllerCompat(activity.window, decorView).show(WindowInsetsCompat.Type.systemBars())
        }
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

    if (uiState.showLoginDialog) {
        LoginDialog(
            onDismiss = {
                viewModel.onEvent(HomeUiEvent.DismissLoginDialog)
            },
            onConfirm = {
                viewModel.onEvent(HomeUiEvent.DismissLoginDialog)
                navigateToLoginWithCleanup()
            }
        )
    }
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

    LaunchedEffect(lazyListState, guestBooks.itemCount, isMediaActive, uiState.audioPlaybackState.isPlaying, uiState.fullscreenVideoUrl) {
        var pendingIndex = -1
        var lastChangedTime = 0L
        if (!isMediaActive || uiState.audioPlaybackState.isPlaying || uiState.fullscreenVideoUrl != null) {
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
        val isRefreshing =
            upcomingInvitations.loadState.mediator?.refresh is LoadState.Loading || guestBooks.loadState.mediator?.refresh is LoadState.Loading

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                upcomingInvitations.refresh()
                guestBooks.refresh()
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
                    onReportClick = { targetId -> onEvent(HomeUiEvent.ShowReport(targetId)) },
                    onFullscreenClick = { url, thumbnailUrl, bounds ->
                        onEvent(HomeUiEvent.ShowFullscreenVideo(url, thumbnailUrl, bounds))
                    }
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
    val sourceLoadState = upcomingInvitations.loadState.source.refresh
    val mediatorLoadState = upcomingInvitations.loadState.mediator?.refresh

    val isRefreshing = sourceLoadState is LoadState.Loading || mediatorLoadState is LoadState.Loading
    val isError = sourceLoadState is LoadState.Error || mediatorLoadState is LoadState.Error
    val isEmpty = !isRefreshing && upcomingInvitations.itemCount == 0

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
            if (upcomingInvitations.itemCount > 0) {
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
            } else {
                when {
                    isRefreshing -> {
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
                        InvitationLoadingIndicator()
                    }

                    isError -> {
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
                        UpcomingStatusContent(
                            title = stringResource(R.string.txt_error_upcoming_title),
                            description = stringResource(R.string.txt_error_upcoming_desc),
                        )
                    }

                    isEmpty -> {
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
                        UpcomingStatusContent(
                            title = stringResource(R.string.txt_empty_upcoming_title),
                            description = stringResource(R.string.txt_empty_upcoming_desc),
                            buttonText = stringResource(R.string.txt_action_create_invitation),
                            onButtonClick = onNavigateToCreate,
                            buttonIconRes = designR.drawable.ic_add_24
                        )
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
                style = NachoTheme.typography.bodyLargeMedium,
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
    onReportClick: (Long) -> Unit,
    onFullscreenClick: (String, String?, Rect) -> Unit,
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

    val sourceLoadState = guestBooks.loadState.source.refresh
    val mediatorLoadState = guestBooks.loadState.mediator?.refresh

    val isRefreshing = sourceLoadState is LoadState.Loading || mediatorLoadState is LoadState.Loading
    val isError = sourceLoadState is LoadState.Error || mediatorLoadState is LoadState.Error
    val isEmpty = !isRefreshing && guestBooks.itemCount == 0

    if (guestBooks.itemCount > 0) {
        items(
            count = guestBooks.itemCount,
            key = { index -> index + GUESTBOOK_KEY_OFFSET }
        ) { index ->
            guestBooks[index]?.let { guestBook ->
                GuestBookItem(
                    modifier = Modifier.animateItem(),
                    guestBook = guestBook,
                    videoPlayerPool = videoPlayerPool,
                    shouldPlayVideo = uiState.canPlayVideo && (index == playVideoIndex),
                    isFromInvitationDetail = false,
                    audioPlaybackState = uiState.audioPlaybackState,
                    isFullscreen = uiState.isFullscreenVideoUrlValid(guestBook),
                    onInvitationTitleClick = {
                        onInvitationTitleClick(
                            guestBook.invitation.id,
                            guestBook.isInvitationOwner,
                        )
                    },
                    onVisualMediaClick = { onVisualMediaClick(it.url) },
                    onAudioMediaClick = { onAudioMediaClick(it.url) },
                    onPlayVideoClick = { url -> onPlayVideoClick(url, guestBook.id) },
                    onReportClick = { onReportClick(guestBook.id) },
                    onFullscreenClick = { url, thumbnailUrl, bounds -> onFullscreenClick(url, thumbnailUrl, bounds) },
                )
            }
        }
    } else {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillParentMaxHeight(0.3f),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isRefreshing -> {
                        GuestBookStatusContent(isLoading = true)
                    }
                    isError -> {
                        GuestBookStatusContent(
                            title = stringResource(R.string.error_msg_failed_load_post)
                        )
                    }
                    isEmpty -> {
                        GuestBookStatusContent(
                            title = stringResource(R.string.txt_empty_new_post_desc)
                        )
                    }
                }
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
    isLoading: Boolean = false,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        if (isLoading) {
            InvitationLoadingIndicator()
        } else {
            title?.let {
                Text(
                    text = it,
                    style = NachoTheme.typography.bodyLargeMedium,
                    color = NachoTheme.colorScheme.textSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = NachoTheme.typography.bodyLargeMedium.lineHeight * 1.4f
                )
            }
        }
    }
}

@PreviewTheme
@Composable
private fun HomeScreenPreview() {
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
