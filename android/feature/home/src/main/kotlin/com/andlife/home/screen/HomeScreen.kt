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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andlife.designsystem.theme.InvitationSpacing
import com.andlife.home.viewmodel.HomeViewModel
import com.andlife.ui.component.guestbook.GuestBookItem
import com.andlife.ui.model.MediaType
import com.andlife.ui.player.VideoPlayerPool
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
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

    // 스크롤 상태 추적
    var isScrolling by remember { mutableStateOf(false) }
    var currentPlayingIndex by remember { mutableStateOf(-1) }

    // 스크롤 멈춤 감지 및 딜레이
    LaunchedEffect(lazyListState.isScrollInProgress) {
        if (lazyListState.isScrollInProgress) {
            isScrolling = true
        } else {
            // 스크롤 멈춤 - 300ms 딜레이 후 재생 시작
            delay(300L)
            if (!lazyListState.isScrollInProgress) {
                isScrolling = false
            }
        }
    }

    // 재생할 비디오 인덱스 결정
    val playVideoIndex by remember {
        derivedStateOf {
            val layoutInfo = lazyListState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo

            // 비디오가 있는 아이템만 필터링하고 가시성 비율 계산
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
                // 스크롤 중일 때
                isScrolling -> {
                    // 현재 재생 중인 비디오의 가시성 확인
                    val currentPlayingVisibility = candidates
                        .find { it.first == currentPlayingIndex }
                        ?.second ?: 0f

                    // 20% 이하면 정지, 아니면 계속 재생
                    if (currentPlayingVisibility < 0.2f) {
                        currentPlayingIndex = -1
                        -1
                    } else {
                        currentPlayingIndex
                    }
                }
                // 스크롤 멈춤 - 중앙 비디오 찾기
                candidates.isEmpty() -> {
                    currentPlayingIndex = -1
                    -1
                }
                else -> {
                    // 가장 많이 보이는 (중앙에 가까운) 비디오 선택
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
