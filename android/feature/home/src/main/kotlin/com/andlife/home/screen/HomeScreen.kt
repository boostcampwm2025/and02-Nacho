package com.andlife.home.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andlife.designsystem.theme.InvitationSpacing
import com.andlife.home.viewmodel.HomeViewModel
import com.andlife.ui.component.guestbook.GuestBookItem
import com.andlife.ui.player.VideoPlayerPool
import kotlinx.collections.immutable.toImmutableList

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        onDispose {
            VideoPlayerPool.releaseAll()
        }
    }

    val lazyListSTate = rememberLazyListState()

    val playVideoIndex by remember {
        derivedStateOf {
            val visibleItems = lazyListSTate.layoutInfo.visibleItemsInfo
            Log.d("HomeScreen", "visibleItems size: ${visibleItems.size}")
            when (visibleItems.size) {
                0 -> -1 // 보이는 아이템이 없으면 -1 반환
                1 -> visibleItems.first().index // 보이는 아이템이 1개면 그 아이템 인덱스 반환
                2 -> {
                    visibleItems.firstOrNull { item ->
                        item.offset + item.size >= item.size * 0.7f // 70% 이상 보이는 아이템 찾기
                    }?.index ?: visibleItems.first().index // 없으면 첫 번째 아이템 인덱스 반환
                }
                else -> {
                    if (visibleItems.size >= 3) {
                        visibleItems[1].index // 3개 이상이면 1 인덱스 반환
                    } else {
                        visibleItems.first().index // 그 외에는 첫 번째 아이템 인덱스 반환
                    }
                }
            }
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
