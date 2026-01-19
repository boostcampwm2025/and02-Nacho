package com.andlife.invitation.screen.guestbook

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.andlife.designsystem.theme.NachoElevation
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.designsystem.theme.NachoTheme
import com.andlife.invitation.model.guestbook.InvitationGuestBookSideEffect
import com.andlife.invitation.model.guestbook.InvitationGuestBookUiEvent
import com.andlife.invitation.model.guestbook.InvitationGuestBookUiState
import com.andlife.invitation.viewmodel.InvitationGuestBookViewModel
import com.andlife.media.video.AutoVideoPlayerPool
import com.andlife.model.common.VideoCandidate
import com.andlife.model.guestbook.GuestBookUiModel
import com.andlife.model.guestbook.MediaUiType
import com.andlife.ui.component.guestbook.GuestBookItem
import com.andlife.ui.component.invitation.InvitationGuestBookForm
import com.andlife.ui.component.paging.PagingStateContent
import com.andlife.ui.util.collectWithLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

@Composable
fun InvitationGuestBookRoute(
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
        snackbarHostState = snackbarHostState,
        videoPlayerPool = viewModel.videoPlayerPool,
        modifier = modifier,
    )
}

@Composable
private fun InvitationGuestBookScreen(
    uiState: InvitationGuestBookUiState,
    guestBooks: LazyPagingItems<GuestBookUiModel>,
    onEvent: (InvitationGuestBookUiEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    videoPlayerPool: AutoVideoPlayerPool,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()
    var playVideoIndex by remember { mutableStateOf(-1) }

    LaunchedEffect(lazyListState, guestBooks.itemCount) {
        var pendingIndex = -1
        var lastChangedTime = 0L

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
        modifier = modifier
            .fillMaxSize(),
        containerColor = NachoTheme.colorScheme.backgroundPrimary,
        bottomBar = {
            Surface(
                tonalElevation = NachoElevation.medium,
                shadowElevation = NachoElevation.large,
                color = NachoTheme.colorScheme.backgroundPrimary
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(NachoSpacing.large)
                        .navigationBarsPadding()
                        .imePadding()
                ) {
                    GuestBookFormSection(uiState = uiState, onEvent = onEvent)
                }
            }
        }
    ) { innerPadding ->
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
                            guestBook = guestBook,
                            videoPlayerPool = videoPlayerPool,
                            shouldPlayVideo = index == playVideoIndex,
                            onInvitationTitleClick = { /* 필요 시 구현 */ },
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

//@Composable
//@Preview
//private fun InvitationGuestBookScreenPreview() {
//    NachoTheme {
//        InvitationGuestBookScreen(
//            uiState = InvitationGuestBookUiState(),
//            guestBooks = flowOf(PagingData.from(emptyList())).collectAsLazyPagingItems(),
//            onEvent = {},
//            snackbarHostState = SnackbarHostState(),
//        )
//    }
//}
