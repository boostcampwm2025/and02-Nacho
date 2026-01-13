package com.andlife.home.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andlife.designsystem.theme.InvitationSpacing
import com.andlife.home.viewmodel.HomeViewModel
import com.andlife.ui.component.guestbook.GuestBookItem
import com.andlife.ui.player.VideoPlayerPool
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val lazyListState = rememberLazyListState()

    var isScrolling by remember { mutableStateOf(false) }
    var currentPlayingIndex by remember { mutableIntStateOf(-1) }
    var lastScrollTime by remember { mutableLongStateOf(0L) }
    var lastScrollOffset by remember { mutableIntStateOf(0) }
    var scrollVelocity by remember { mutableFloatStateOf(0f) }

    val SLOW_SCROLL_THRESHOLD = 50f 

    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemScrollOffset }
            .collect { offset ->
                val currentTime = System.currentTimeMillis()
                val timeDiff = (currentTime - lastScrollTime).coerceAtLeast(1)
                val offsetDiff = abs(offset - lastScrollOffset)

                scrollVelocity = (offsetDiff.toFloat() / timeDiff) * 1000 // px/s

                lastScrollTime = currentTime
                lastScrollOffset = offset
            }
    }

    LaunchedEffect(lazyListState.isScrollInProgress) {
        if (lazyListState.isScrollInProgress) {
            isScrolling = true
        } else {
            delay(300) // 스크롤 완전히 멈춘 후 대기
            if (!lazyListState.isScrollInProgress) {
                isScrolling = false
            }
        }
    }

    val playVideoIndex by remember {
        derivedStateOf {
            val layoutInfo = lazyListState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo

            val candidates = visibleItems.mapNotNull { item ->
                val guestBook = uiState.value.guestBooks.getOrNull(item.index)
                if (guestBook?.visualMedias?.isNotEmpty() != true) return@mapNotNull null

                val visibleHeight =
                    min(item.offset + item.size, layoutInfo.viewportEndOffset) -
                        max(item.offset, layoutInfo.viewportStartOffset)

                val ratio = visibleHeight.toFloat() / item.size
                item.index to ratio
            }

            when {
                candidates.isEmpty() -> {
                    currentPlayingIndex = -1
                    -1
                }
                // 스크롤 중
                isScrolling -> {
                    val currentPlayingVisibility = candidates
                        .find { it.first == currentPlayingIndex }
                        ?.second ?: 0f

                    // 현재 재생 중인 영상이 20% 이하면 무조건 정지
                    if (currentPlayingVisibility < 0.2f) {
                        currentPlayingIndex = -1
                    }

                    // 스크롤 속도가 느리면 (손 댄 상태로 천천히) → 중앙 영상 재생
                    if (scrollVelocity < SLOW_SCROLL_THRESHOLD) {
                        val centerIndex = candidates
                            .maxByOrNull { it.second }
                            ?.first ?: -1
                        currentPlayingIndex = centerIndex
                        centerIndex
                    } else {
                        // 빠른 스크롤 (손 댄 상태로 빠르게) → 기존 재생 유지만
                        currentPlayingIndex
                    }
                }
                // 스크롤 멈춤 (손 뗀 후) - 중앙 비디오 재생
                else -> {
                    val centerIndex = candidates
                        .maxByOrNull { it.second }
                        ?.first ?: -1

                    currentPlayingIndex = centerIndex
                    centerIndex
                }
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> VideoPlayerPool.resumeLastPlayed()
                Lifecycle.Event.ON_PAUSE -> VideoPlayerPool.pauseAllPlayers()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.padding(horizontal = InvitationSpacing.large),
            verticalArrangement = Arrangement.spacedBy(InvitationSpacing.large),
        ) {
            itemsIndexed(
                items = uiState.value.guestBooks,
                key = { _, guestBook -> guestBook.id },
            ) { index, guestBook ->
                GuestBookItem(
                    guestBookId = guestBook.id,
                    authorName = guestBook.author.name,
                    createdAt = guestBook.createdAt,
                    textContent = guestBook.textContent,
                    visualMediaUrls = guestBook.visualMedias.toImmutableList(),
                    audioMediaUrls = guestBook.audioMedias.toImmutableList(),
                    totalVisualCount = guestBook.totalVisualCount,
                    authorProfileImageUrl = guestBook.author.profileImageUrl,
                    invitationTitle = guestBook.invitation.title,
                    invitationId = guestBook.invitation.id,
                    isAuthorSelf = guestBook.isAuthorSelf,
                    shouldPlayVideo = index == playVideoIndex,
                )
            }
        }
    }
}
