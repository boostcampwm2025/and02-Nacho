package com.andlife.invitation.screen.guestbook

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.andlife.designsystem.preview.PreviewTheme
import com.andlife.designsystem.theme.NachoElevation
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
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
    val snackbarHostState = remember { SnackbarHostState() }
    val lifecycleOwner = LocalLifecycleOwner.current

    viewModel.effectFlow.collectWithLifecycle { effect ->
        when (effect) {
            is InvitationGuestBookSideEffect.ShowSnackbar -> {
                snackbarHostState.showSnackbar(
                    message = effect.message,
                    duration = SnackbarDuration.Short,
                )
            }

            is InvitationGuestBookSideEffect.CreateGuestBookSuccess -> {
                guestBooks.refresh()
            }
        }
    }

    DisposableEffect(Unit) {
        viewModel.videoPlayerPool.preparePlayers()
        onDispose {
            viewModel.videoPlayerPool.releaseAllPlayers()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> viewModel.videoPlayerPool.resumeLastPlayed()
                Lifecycle.Event.ON_PAUSE -> viewModel.videoPlayerPool.pauseAllPlayers()
                Lifecycle.Event.ON_DESTROY -> viewModel.videoPlayerPool.resetPool()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    InvitationGuestBookScreen(
        uiState = uiState,
        guestBooks = guestBooks,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack,
        snackbarHostState = snackbarHostState,
        videoPlayerPool = viewModel.videoPlayerPool,
        modifier = modifier,
    )
}

@Composable
private fun InvitationGuestBookScreen(
    uiState: InvitationGuestBookUiState,
    guestBooks: LazyPagingItems<GuestBookUiModel>,
    snackbarHostState: SnackbarHostState,
    videoPlayerPool: AutoVideoPlayerPool,
    onEvent: (InvitationGuestBookUiEvent) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var playVideoIndex by remember { mutableStateOf(-1) }

    // 뒤로가기 시 미디어 정리를 위한 상태 플래그
    var isMediaActive by remember { mutableStateOf(true) }

    // 뒤로가기 로직: 상태를 먼저 끄고 지연 후 실제 이동
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

    LaunchedEffect(lazyListState, guestBooks.itemCount, isMediaActive) {
        if (!isMediaActive) {
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

                    val guestBook = try { guestBooks.peek(dataIndex) } catch (e: Exception) { null }
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

                if (shouldChangeTo != playVideoIndex) {
                    delay(200L)
                    playVideoIndex = shouldChangeTo
                }
            }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = NachoTheme.colorScheme.backgroundPrimary,
        bottomBar = {
            Surface(
                tonalElevation = NachoElevation.medium,
                color = NachoTheme.colorScheme.backgroundPrimary
            ) {
                Box(modifier = Modifier.navigationBarsPadding().imePadding()) {
                    GuestBookFormSection(uiState = uiState, onEvent = onEvent)
                }
            }
        }
    ) { innerPadding ->
        if (isMediaActive) {
            PagingStateContent(
                loadState = guestBooks.loadState.refresh,
                itemCount = guestBooks.itemCount,
                onRetry = { guestBooks.retry() },
            ) {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(NachoSpacing.large),
                    contentPadding = PaddingValues(
                        top = NachoSpacing.large,
                        bottom = innerPadding.calculateBottomPadding()
                    )
                ) {
                    items(
                        count = guestBooks.itemCount,
                        key = guestBooks.itemKey { it.id }
                    ) { index ->
                        guestBooks[index]?.let { guestBook ->
                            GuestBookItem(
                                modifier = Modifier.animateItem(),
                                guestBook = guestBook,
                                videoPlayerPool = videoPlayerPool,
                                shouldPlayVideo = isMediaActive && (index == playVideoIndex),
                                onVisualMediaClick = { onEvent(InvitationGuestBookUiEvent.ClickVisualMedia(it.url)) },
                                onAudioMediaClick = { onEvent(InvitationGuestBookUiEvent.ClickAudioMedia(it.url)) },
                                onMenuClick = { onEvent(InvitationGuestBookUiEvent.ClickGuestBookMenu(guestBook.id)) },
                            )
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
        selectedMedias = uiState.selectedMedias,
        textContent = uiState.textContent,
        isUploading = uiState.isUploading,
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
