package com.andlife.invitation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andlife.invitation.component.InvitationMediaGridView
import com.andlife.invitation.model.guestbook.InvitationCollectionUiState
import com.andlife.invitation.viewmodel.InvitationCollectionViewModel

@Composable
fun InvitationRoute(
    onNavigateToDetail: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InvitationCollectionViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    InvitationScreen(
        uiState = uiState,
        onNavigateToDetail = onNavigateToDetail,
        modifier = modifier,
    )
}

@Composable
private fun InvitationScreen(
    uiState: InvitationCollectionUiState,
    onNavigateToDetail: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    // TODO: 초대장 리스트 화면
    // - 로딩 상태 처리 필요 (uiState.isLoading)
    // - 에러 상태 처리 필요
    // - 빈 상태 처리 필요 (초대장이 없을 때)
    // - Scaffold, TopAppBar 등 추가 필요
    InvitationMediaGridView(
        items = uiState.mediaItems,
        onItemClick = onNavigateToDetail,
        modifier = modifier,
    )
}
