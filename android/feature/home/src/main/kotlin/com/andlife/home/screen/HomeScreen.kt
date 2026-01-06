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
            val visibleItems = lazyListSTate.layoutInfo.visibleItemsInfo

            val visibleItemsWithVisualMedia = visibleItems.filter { itemInfo ->
                val guestBook = uiState.value.guestBooks.getOrNull(itemInfo.index)
                guestBook?.visualMedias?.isNotEmpty() == true
            }

            when (visibleItemsWithVisualMedia.size) {
                0 -> -1 // 보이는 아이템이 없으면 -1 반환
                1 -> visibleItemsWithVisualMedia.first().index // 보이는 아이템이 1개면 그 아이템 인덱스 반환
                2 -> {
                    visibleItemsWithVisualMedia.firstOrNull { item ->
                        item.offset + item.size >= item.size * 0.7f // 70% 이상 보이는 아이템 찾기
                    }?.index ?: visibleItemsWithVisualMedia.first().index // 없으면 첫 번째 아이템 인덱스 반환
                }
                else -> {
                    if (visibleItemsWithVisualMedia.size >= 3) {
                        visibleItemsWithVisualMedia[1].index // 3개 이상이면 1 인덱스(두 번째 아이템) 반환
                    } else {
                        visibleItemsWithVisualMedia.first().index
                    }
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
