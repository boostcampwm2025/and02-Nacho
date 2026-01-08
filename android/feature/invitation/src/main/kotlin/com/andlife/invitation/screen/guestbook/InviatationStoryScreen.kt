package com.andlife.invitation.screen.guestbook

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andlife.designsystem.theme.InvitationSpacing
import com.andlife.designsystem.theme.InvitationTheme
import com.andlife.invitation.component.StoryPageContent
import com.andlife.invitation.component.StoryTopHeader
import com.andlife.invitation.model.guestbook.collection.InvitationCollectionUiEvent
import com.andlife.invitation.viewmodel.InvitationCollectionViewModel

@Composable
fun InvitationStoryScreen(
    viewModel: InvitationCollectionViewModel,
    initialIndex: Int,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(
        initialPage = initialIndex,
        pageCount = { uiState.mediaItems.size }
    )

    LaunchedEffect(pagerState.currentPage) {
        viewModel.onEvent(InvitationCollectionUiEvent.PageChanged(pagerState.currentPage))
    }

    val currentItem = uiState.mediaItems.getOrNull(pagerState.currentPage)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(InvitationTheme.colorScheme.backgroundInverse)
    ) {
        currentItem?.let { item ->
            StoryTopHeader(
                name = item.authorName,
                date = item.createdAt,
                profileUrl = item.authorProfileUrl,
                onClose = onClose
            )
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            pageSpacing = InvitationSpacing.none,
            userScrollEnabled = true
        ) { pageIndex ->
            val item = uiState.mediaItems[pageIndex]

            Box(modifier = Modifier.fillMaxSize()) {
                StoryPageContent(
                    item = item,
                    isExpanded = uiState.isTextExpanded,
                    onToggleExpand = { viewModel.onEvent(InvitationCollectionUiEvent.ToggleExpand) },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}
