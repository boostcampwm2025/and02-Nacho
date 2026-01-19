package com.andlife.home.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andlife.designsystem.theme.NachoSpacing
import com.andlife.home.model.HomeSideEffect
import com.andlife.home.model.HomeUiEvent
import com.andlife.home.model.HomeUiState
import com.andlife.home.viewmodel.HomeViewModel
import com.andlife.media.video.AutoVideoPlayerPool
import com.andlife.model.guestbook.MediaUiType
import com.andlife.ui.component.guestbook.GuestBookItem
import com.andlife.ui.util.collectWithLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

data class VideoCandidate(
    val index: Int,
    val visibilityRatio: Float,
)

@Composable
fun HomeRoute(
    onNavigateToInvitationDetail: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    viewModel.effectFlow.collectWithLifecycle { effect ->
        when (effect) {
            is HomeSideEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
            is HomeSideEffect.NavigateToInvitationDetail -> onNavigateToInvitationDetail(effect.invitationId)
        }
    }

    HomeScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState,
        videoPlayerPool = viewModel.videoPlayerPool,
        modifier = modifier,
    )
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onEvent: (HomeUiEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    videoPlayerPool: AutoVideoPlayerPool,
    modifier: Modifier = Modifier,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lazyListState = rememberLazyListState()
    var playVideoIndex by remember { mutableStateOf(-1) }

    LaunchedEffect(lazyListState, uiState.guestBooks) {
        var pendingIndex = -1
        var lastChangedTime = 0L

        snapshotFlow { lazyListState.layoutInfo }
            .collect { layoutInfo ->
                val visibleItems = layoutInfo.visibleItemsInfo
                if (visibleItems.isEmpty()) return@collect

                val videoCandidates = visibleItems.mapNotNull { itemInfo ->
                    val guestBook = uiState.guestBooks.getOrNull(itemInfo.index)
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

                    playVideoIndex == -1 ->
                        if (bestVisibilityRatio >= 0.6f) bestIndex else -1

                    bestIndex != playVideoIndex &&
                        bestVisibilityRatio > currentPlayingRatio + 0.3f -> bestIndex

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

    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> videoPlayerPool.resumeLastPlayed()
                    Lifecycle.Event.ON_PAUSE -> videoPlayerPool.pauseAllPlayers()
                    Lifecycle.Event.ON_DESTROY -> videoPlayerPool.resetPool()
                    else -> {}
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        LazyColumn(
            state = lazyListState,
            modifier =
                Modifier
                    .padding(innerPadding)
                    .padding(horizontal = NachoSpacing.large),
            verticalArrangement = Arrangement.spacedBy(NachoSpacing.large),
        ) {
            itemsIndexed(
                items = uiState.guestBooks,
                key = { _, guestBook -> guestBook.id },
            ) { index, guestBook ->
                GuestBookItem(
                    guestBook = guestBook,
                    videoPlayerPool = videoPlayerPool,
                    shouldPlayVideo = index == playVideoIndex,
                    onInvitationTitleClick = {
                        onEvent(
                            HomeUiEvent.ClickInvitationTitle(guestBook.invitation?.id ?: -1L),
                        )
                    },
                    onVisualMediaClick = { onEvent(HomeUiEvent.ClickVisualMedia(it.url)) },
                    onAudioMediaClick = { onEvent(HomeUiEvent.ClickAudioMedia(it.url)) },
                    onMenuClick = { onEvent(HomeUiEvent.ClickGuestBookMenu(guestBook.id)) },
                )
            }
        }
    }
}
