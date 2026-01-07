package com.andlife.invitation.screen.guestbook

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andlife.designsystem.theme.InvitationSpacing
import com.andlife.designsystem.theme.InvitationTheme
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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(InvitationTheme.colorScheme.backgroundPrimary)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            pageSpacing = InvitationSpacing.none,
            userScrollEnabled = true
        ) { pageIndex ->

        }
    }

}

