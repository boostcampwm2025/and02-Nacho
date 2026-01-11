package com.andlife.home.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import kotlin.math.max
import kotlin.math.min

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    val lazyListSTate = rememberLazyListState()

    val playVideoIndex by remember {
        derivedStateOf {
            val layoutInfo = lazyListSTate.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo

            // visualMedia가 있는 아이템만 필터링하면서 가시성 비율도 함께 계산
            val candidates = visibleItems.mapNotNull { item ->
                val guestBook = uiState.value.guestBooks.getOrNull(item.index)
                if (guestBook?.visualMedias?.isNotEmpty() != true) return@mapNotNull null

                // 실제 화면에 보이는 높이 계산
                val visibleHeight =
                    min(item.offset + item.size, layoutInfo.viewportEndOffset) -
                        max(item.offset, layoutInfo.viewportStartOffset)

                val ratio = visibleHeight.toFloat() / item.size

                item.index to ratio
            }

            when {
                candidates.isEmpty() -> -1
                candidates.size == 1 -> {
                    // 1개만 보일 때도 최소 30% 이상은 보여야 재생
                    if (candidates.first().second >= 0.9f) {
                        candidates.first().first
                    } else {
                        -1
                    }
                }
                else -> {
                    // 70% 이상 보이는 것 중 가장 많이 보이는 것
                    candidates
                        .filter { it.second >= 0.7f }
                        .maxByOrNull { it.second }
                        ?.first
                        ?: candidates.maxByOrNull { it.second }!!.first
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
            state = lazyListSTate,
            modifier =
                Modifier
                    .padding(horizontal = InvitationSpacing.large),
            verticalArrangement = Arrangement.spacedBy(InvitationSpacing.large),
        ) {
            itemsIndexed(
                items = uiState.value.guestBooks,
                key = { _, guestBook -> guestBook.id },
            ) { index, guestBook ->
                GuestBookItem(
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
