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
        onNavigateToDetail = onNavigateToDetail,
        uiState = uiState,
        modifier = modifier,
    )
}

@Composable
private fun InvitationScreen(
    onNavigateToDetail: (Long) -> Unit,
    uiState: InvitationCollectionUiState,
    modifier: Modifier = Modifier,
) {
    InvitationMediaGridView(
        uiState.mediaItems,
        onItemClick = {},
    )
}
