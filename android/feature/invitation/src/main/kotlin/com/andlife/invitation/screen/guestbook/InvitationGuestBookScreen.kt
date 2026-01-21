package com.andlife.invitation.screen.guestbook

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.stringResource
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
import com.andlife.designsystem.component.dialog.NachoDialog
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoElevation
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.invitation.R
import com.andlife.invitation.model.guestbook.InvitationGuestBookSideEffect
import com.andlife.invitation.model.guestbook.InvitationGuestBookUiEvent
import com.andlife.invitation.model.guestbook.InvitationGuestBookUiState
import com.andlife.invitation.viewmodel.InvitationGuestBookViewModel
import com.andlife.media.video.AutoVideoPlayer
import com.andlife.media.video.AutoVideoPlayerPool
import com.andlife.model.common.AuthorUiModel
import com.andlife.model.common.VideoCandidate
import com.andlife.model.guestbook.GuestBookInvitationUiModel
import com.andlife.model.guestbook.GuestBookMediaUiModel
import com.andlife.model.guestbook.GuestBookUiModel
import com.andlife.model.guestbook.MediaUiType
import com.andlife.ui.component.guestbook.GuestBookItem
import com.andlife.ui.component.invitation.InvitationGuestBookForm
import com.andlife.ui.component.paging.PagingStateContent
import com.andlife.ui.util.collectWithLifecycle
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlin.math.max
import kotlin.math.min

@Composable
fun InvitationGuestBookRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InvitationGuestBookViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val guestBooks = viewModel.guestBooksPagingFlow.collectAsLazyPagingItems()

    val lifecycleOwner = LocalLifecycleOwner.current
    val lazyListState = rememberLazyListState()

    val snackbarHostState = remember { SnackbarHostState() }
    var showDeleteDialog by remember { mutableStateOf<Long?>(null) }
    var scrollToTop by remember { mutableStateOf(false) }

    viewModel.effectFlow.collectWithLifecycle { effect ->
        when (effect) {
            is InvitationGuestBookSideEffect.ShowSnackbar -> {
                snackbarHostState.showSnackbar(
                    message = effect.message,
                    duration = SnackbarDuration.Short,
                )
            }

            is InvitationGuestBookSideEffect.CreateGuestBookSuccess -> {
                scrollToTop = true
                viewModel.invalidateGuestBooks()
            }

            is InvitationGuestBookSideEffect.UpdateGuestBookSuccess -> {
                viewModel.invalidateGuestBooks()
            }

            is InvitationGuestBookSideEffect.DeleteGuestBookSuccess -> {
                viewModel.invalidateGuestBooks()
            }
        }
    }

    LaunchedEffect(guestBooks.loadState.refresh, scrollToTop) {
        if (scrollToTop && guestBooks.loadState.refresh is LoadState.NotLoading) {
            if (guestBooks.itemCount > 0) {
                lazyListState.animateScrollToItem(0)
            }
            scrollToTop = false
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

    if (showDeleteDialog != null) {
        NachoDialog(
            onDismiss = { showDeleteDialog = null }
        ) {
            Column(
                modifier = Modifier.padding(NachoSpacing.xLarge),
                verticalArrangement = Arrangement.spacedBy(NachoSpacing.medium)
            ) {
                Text(
                    text = stringResource(R.string.txt_delete_dialog_title),
                    color = NachoTheme.colorScheme.textPrimary,
                    style = NachoTheme.typography.headingSmallSemiBold,
                )
                Spacer(modifier = Modifier.padding(NachoSpacing.xSmall))
                Text(
                    text = stringResource(R.string.txt_delete_dialog_message),
                    color = NachoTheme.colorScheme.textSecondary,
                    style = NachoTheme.typography.bodyMediumRegular,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = { showDeleteDialog = null }
                    ) {
                        Text(
                            text = stringResource(R.string.btn_label_cancel),
                            color = NachoTheme.colorScheme.textPrimary,
                            style = NachoTheme.typography.bodyMediumSemiBold,
                        )
                    }
                    TextButton(
                        onClick = {
                            showDeleteDialog?.let { guestBookId ->
                                viewModel.onEvent(InvitationGuestBookUiEvent.ClickDeleteMenu(guestBookId))
                            }
                            showDeleteDialog = null
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.btn_label_delete),
                            color = NachoTheme.colorScheme.brandDark,
                            style = NachoTheme.typography.bodyMediumSemiBold,
                        )
                    }
                }
            }
        }
    }

    InvitationGuestBookScreen(
        uiState = uiState,
        guestBooks = guestBooks,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack,
        snackbarHostState = snackbarHostState,
        lazyListState = lazyListState,
        videoPlayerPool = viewModel.videoPlayerPool,
        onDeleteMenuClick = { guestBookId -> showDeleteDialog = guestBookId },
        modifier = modifier,
    )
}

@Composable
private fun InvitationGuestBookScreen(
    uiState: InvitationGuestBookUiState,
    guestBooks: LazyPagingItems<GuestBookUiModel>,
    onEvent: (InvitationGuestBookUiEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    lazyListState: LazyListState,
    videoPlayerPool: AutoVideoPlayerPool,
    onNavigateBack: () -> Unit,
    onDeleteMenuClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    var playVideoIndex by remember { mutableStateOf(-1) }
    var isMediaActive by remember { mutableStateOf(true) }

    val navigateBackWithCleanup: () -> Unit = {
        isMediaActive = false
        coroutineScope.launch {
            videoPlayerPool.pauseAllPlayers()
            onEvent(InvitationGuestBookUiEvent.ClickAudioMedia(""))

            delay(50L)
            onNavigateBack()
        }
    }

    BackHandler(onBack = navigateBackWithCleanup)

    LaunchedEffect(lazyListState, guestBooks.itemCount, isMediaActive, uiState.isAudioPlaying) {
        var pendingIndex = -1
        var lastChangedTime = 0L
        if (!isMediaActive || uiState.isAudioPlaying) {
            playVideoIndex = -1
            return@LaunchedEffect
        }

        snapshotFlow { lazyListState.layoutInfo }
            .collect { layoutInfo ->
                val visibleItems = layoutInfo.visibleItemsInfo
                if (visibleItems.isEmpty()) return@collect

                val videoCandidates = visibleItems.mapNotNull { itemInfo ->
                    val dataIndex = itemInfo.index

                    if (dataIndex < 0 || dataIndex >= guestBooks.itemCount) return@mapNotNull null

                    val guestBook = try {
                        guestBooks.peek(dataIndex)
                    } catch (e: Exception) {
                        null
                    }

                    val hasVideo = guestBook?.visualMedias?.any { it.type == MediaUiType.VIDEO } == true
                    if (!hasVideo) return@mapNotNull null

                    val visibleHeight = min(itemInfo.offset + itemInfo.size, layoutInfo.viewportEndOffset) -
                        max(itemInfo.offset, layoutInfo.viewportStartOffset)
                    val visibilityRatio = visibleHeight.toFloat() / itemInfo.size

                    VideoCandidate(itemInfo.index, visibilityRatio)
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
        containerColor = NachoTheme.colorScheme.backgroundPrimary,
        bottomBar = {
            Surface(
                tonalElevation = NachoElevation.medium,
                shadowElevation = NachoElevation.large,
                color = NachoTheme.colorScheme.backgroundPrimary
            ) {
                Box(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .imePadding()
                ) {
                    GuestBookFormSection(uiState = uiState, onEvent = onEvent)
                }
            }
        }
    ) { innerPadding ->
        if (isMediaActive) {
            val isInitialLoading = guestBooks.loadState.refresh is LoadState.Loading && guestBooks.itemCount == 0

            if (isInitialLoading || guestBooks.itemCount == 0) {
                PagingStateContent(
                    loadState = guestBooks.loadState.refresh,
                    itemCount = guestBooks.itemCount,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding()),
                    onRetry = { guestBooks.retry() }
                ) {}
            } else {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        bottom = innerPadding.calculateBottomPadding()
                    )
                ) {
                    items(
                        count = guestBooks.itemCount,
                        key = guestBooks.itemKey { it.id }
                    ) { index ->
                        guestBooks[index]?.let { guestBook ->
                            GuestBookItem(
                                modifier = Modifier
                                    .animateItem(),
                                guestBook = guestBook,
                                videoPlayerPool = videoPlayerPool,
                                shouldPlayVideo = isMediaActive && (index == playVideoIndex),
                                isAudioPlaying = uiState.isAudioPlaying &&
                                    guestBook.audioMedias.any { it.url == uiState.playingAudioUrl },
                                playingAudioUrl = uiState.playingAudioUrl,
                                isEditing = uiState.editingGuestBookId == guestBook.id,
                                onEditClick = { onEvent(InvitationGuestBookUiEvent.ClickEditMenu(guestBook)) },
                                onDeleteClick = { onDeleteMenuClick(guestBook.id) },
                                onVisualMediaClick = { onEvent(InvitationGuestBookUiEvent.ClickVisualMedia(it.url)) },
                                onAudioMediaClick = { onEvent(InvitationGuestBookUiEvent.ClickAudioMedia(it.url)) },
                                onMenuClick = { onEvent(InvitationGuestBookUiEvent.ClickGuestBookMenu(guestBook.id)) },
                            )
                        }
                    }

                    if (guestBooks.loadState.append is LoadState.Loading) {
                        item {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(NachoSpacing.medium),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GuestBookFormSection(
    uiState: InvitationGuestBookUiState,
    onEvent: (InvitationGuestBookUiEvent) -> Unit,
) {
    InvitationGuestBookForm(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = NachoSpacing.large,
                vertical = NachoSpacing.xSmall,
            ),
        selectedMedias = uiState.selectedMedias,
        textContent = uiState.textContent,
        isUploading = uiState.isUploading,
        isSubmittable = uiState.isSubmittable,
        onMediasSelected = { medias ->
            onEvent(InvitationGuestBookUiEvent.UpdateSelectedMedias(medias))
        },
        onMediaRemove = { media ->
            onEvent(InvitationGuestBookUiEvent.RemoveMedia(media))
        },
        onTextContentChange = { text ->
            onEvent(InvitationGuestBookUiEvent.UpdateTextContent(text))
        },
        onUploadClick = {
            onEvent(InvitationGuestBookUiEvent.UploadMedias)
        },
    )
}

@PreviewTheme
@Composable
private fun InvitationGuestBookEmptyPreview() {
    val emptyGuestBooks = flowOf(PagingData.empty<GuestBookUiModel>()).collectAsLazyPagingItems()
    NachoTheme {
        InvitationGuestBookScreen(
            uiState = InvitationGuestBookUiState(isLoadingGuestBooks = false),
            guestBooks = emptyGuestBooks,
            onEvent = {},
            onNavigateBack = {},
            videoPlayerPool = FakeVideoPlayerPool(),
            snackbarHostState = SnackbarHostState(),
            lazyListState = rememberLazyListState(),
            onDeleteMenuClick = {},
        )
    }
}

@PreviewTheme
@Composable
private fun InvitationGuestBookResultPreview() {
    val fakeGuestBooks = listOf(
        GuestBookUiModel(
            id = 1L,
            invitation = GuestBookInvitationUiModel(id = 222L, title = "우리 결혼해요!"),
            author = AuthorUiModel(id = 111L, name = "홍길동", profileImageUrl = null),
            textContent = "결혼 축하드려요! 행복하게 잘 사세요~!",
            visualMedias = emptyList<GuestBookMediaUiModel>().toImmutableList(),
            audioMedias = listOf(
                GuestBookMediaUiModel(
                    id = 10L,
                    type = MediaUiType.AUDIO,
                    url = "https://example.com/audio.m4a",
                    durationSeconds = 15,
                    displayOrder = 1
                )
            ).toImmutableList(),
            totalVisualCount = 0,
            isOwner = true,
            createdAt = LocalDateTime(2026, 1, 20, 10, 0),
            updatedAt = LocalDateTime(2026, 1, 20, 10, 0),
        ),
        GuestBookUiModel(
            id = 2L,
            author = AuthorUiModel(id = 112L, name = "이순신", profileImageUrl = null),
            textContent = "직접 가서 축하해주고 싶었는데 아쉽네요. 멀리서나마 응원합니다!",
            visualMedias = emptyList<GuestBookMediaUiModel>().toImmutableList(),
            audioMedias = emptyList<GuestBookMediaUiModel>().toImmutableList(),
            totalVisualCount = 0,
            isOwner = false,
            createdAt = LocalDateTime(2026, 1, 19, 15, 30),
            updatedAt = LocalDateTime(2026, 1, 19, 15, 30),
        )
    )

    NachoTheme {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(NachoSpacing.large),
            verticalArrangement = Arrangement.spacedBy(NachoSpacing.large)
        ) {
            items(fakeGuestBooks.size) { index ->
                GuestBookItem(
                    guestBook = fakeGuestBooks[index],
                    videoPlayerPool = FakeVideoPlayerPool(),
                    shouldPlayVideo = false,
                    isAudioPlaying = false,
                    playingAudioUrl = null,
                    onInvitationTitleClick = {},
                    onVisualMediaClick = {},
                    onAudioMediaClick = {},
                    onMenuClick = {},
                )
            }
        }
    }
}

class FakeVideoPlayerPool : AutoVideoPlayerPool {
    override fun preparePlayers() {}
    override fun getPlayer(url: String): AutoVideoPlayer {
        throw NotImplementedError()
    }

    override fun playPlayer(url: String, itemId: Long) {}
    override fun pausePlayer(url: String) {}
    override fun pauseAllPlayers() {}
    override fun resumeLastPlayed() {}
    override fun resetPool() {}
    override fun releaseAllPlayers() {}
}
