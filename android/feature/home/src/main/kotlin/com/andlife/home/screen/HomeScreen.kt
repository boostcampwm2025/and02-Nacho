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
import com.andlife.home.viewmodel.HomeSideEffect
import com.andlife.home.viewmodel.HomeUiEvent
import com.andlife.home.viewmodel.HomeUiState
import com.andlife.home.viewmodel.HomeViewModel
import com.andlife.model.guestbook.MediaUiType
import com.andlife.ui.component.guestbook.GuestBookItem
import com.andlife.ui.player.VideoPlayerPool
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
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    viewModel.effectFlow.collectWithLifecycle { effect ->
        when (effect) {
            is HomeSideEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
        }
    }

    HomeScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onEvent: (HomeUiEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val lazyListState = rememberLazyListState()
    var playVideoIndex by remember { mutableStateOf(-1) }

    LaunchedEffect(lazyListState, uiState.guestBooks) {
        var pendingIndex = -1 // 재생 대기 중인 인덱스 초기화
        var lastChangedTime = 0L // 마지막으로 변경된 시간 초기화

        snapshotFlow { lazyListState.layoutInfo } // layout 정보를 관찰함
            .collect { layoutInfo ->
                val visibleItems = layoutInfo.visibleItemsInfo
                if (visibleItems.isEmpty()) return@collect

//                val videoCandidates = visibleItems.filter { itemInfo ->
//                    val item = uiState.guestBooks.getOrNull(itemInfo.index)
//                    val hasVideo = item?.visualMedias?.any { it.type == MediaUiType.VIDEO } == true
//                    hasVideo
//                }
                val videoCandidates = visibleItems.mapNotNull { itemInfo ->
                    val guestBook = uiState.guestBooks.getOrNull(itemInfo.index)
                    val hasVideo = guestBook?.visualMedias?.any { it.type == MediaUiType.VIDEO } == true
                    if (!hasVideo) return@mapNotNull null

                    val visibleHeight = min(itemInfo.offset + itemInfo.size, layoutInfo.viewportEndOffset) -
                        max(itemInfo.offset, layoutInfo.viewportStartOffset)
                    val visibilityRatio = visibleHeight.toFloat() / itemInfo.size
//                    itemInfo.index to visibilityRatio // 인덱스와 가시성 비율을 Pair로 반환(예: (index, 0.75f)) -> 0번 인덱스가 75% 보임
                    VideoCandidate(itemInfo.index, visibilityRatio)
                }

                if (videoCandidates.isEmpty()) {
                    playVideoIndex = -1 // 만약 보이는 비디오 후보가 없다면 pendingIndex를 -1로 설정 -> 재생할 비디오 없음
                    return@collect
                }

                val (bestIndex, bestVisibilityRatio) = videoCandidates.maxBy { it.visibilityRatio } // 가장 많이 보이는 비디오 후보 선택
                val currentPlayingItem = videoCandidates.find { it.index == playVideoIndex }
                val currentPlayingRatio = currentPlayingItem?.visibilityRatio ?: 0f

                val shouldChangeTo = when {
                    // 1. 현재 영상 거의 안 보이면 즉시 정지
                    playVideoIndex != -1 && currentPlayingRatio < 0.2f -> -1

                    // 2. 재생 중인 게 없을 때만 새 후보 탐색
                    playVideoIndex == -1 ->
                        if (bestVisibilityRatio >= 0.6f) bestIndex else -1

                    // 3. 더 잘 보이는 영상이 충분히 우세하면 교체
                    bestIndex != playVideoIndex &&
                        bestVisibilityRatio > currentPlayingRatio + 0.3f -> bestIndex

                    else -> playVideoIndex
                }


                if (shouldChangeTo == -1 && playVideoIndex != -1) { // 재생 중인 비디오가 있는데, 이제 재생할 비디오가 없을 때
                    // 재생할 비디오가 변경되는 경우, 즉시 변경
                    playVideoIndex = -1
                    pendingIndex = -1
                } else if (shouldChangeTo != playVideoIndex && shouldChangeTo != pendingIndex) { // 재생되어야 하는 비디오가 현재 재생 중인 비디오와 다르고, 대기 중인 인덱스와도 다를 때
                    pendingIndex = shouldChangeTo // 재생 대기 인덱스 설정
                    lastChangedTime = System.currentTimeMillis()
                    launch {
                        delay(200L)
                        // 200ms 후에도 동일한 대기 인덱스라면 재생 변경
                        if (pendingIndex == shouldChangeTo && System.currentTimeMillis() - lastChangedTime >= 200L) {
                            playVideoIndex = shouldChangeTo
//                            pendingIndex = -1
                        }
                    }
                }
            }
    }

    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> VideoPlayerPool.resumeLastPlayed()
                    Lifecycle.Event.ON_PAUSE -> VideoPlayerPool.pauseAllPlayers()
                    Lifecycle.Event.ON_DESTROY -> VideoPlayerPool.releaseAll()
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
