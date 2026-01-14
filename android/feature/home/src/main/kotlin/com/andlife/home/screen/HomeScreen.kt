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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
import com.andlife.ui.component.guestbook.GuestBookItem
import com.andlife.ui.player.VideoPlayerPool
import com.andlife.ui.util.collectWithLifecycle

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
    val lifecycleOwner = LocalLifecycleOwner.current
    val lazyListSTate = rememberLazyListState()

    val playVideoIndex by remember(uiState) {
        derivedStateOf {
            val visibleItems = lazyListSTate.layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty()) return@derivedStateOf -1

            val visibleItemsWithVisualMedia = visibleItems.filter { itemInfo ->
                val guestBook = uiState.guestBooks.getOrNull(itemInfo.index)
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
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        LazyColumn(
            state = lazyListSTate,
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
                    onInvitationTitleClick = { onEvent(HomeUiEvent.ClickInvitationTitle(guestBook.invitation.id)) },
                    onVisualMediaClick = { onEvent(HomeUiEvent.ClickVisualMedia(it.url)) },
                    onAudioMediaClick = { onEvent(HomeUiEvent.ClickAudioMedia(it.url)) },
                    onMenuClick = { onEvent(HomeUiEvent.ClickGuestBookMenu(guestBook.id)) },
                )
            }
        }
    }
}
