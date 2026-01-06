package com.andlife.invitation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andlife.invitation.viewmodel.InvitationCollectionViewModel
import com.andlife.invitation.component.InvitationMediaGridView

@Composable
fun InvitationScreen(
    modifier: Modifier = Modifier,
    viewModel: InvitationCollectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    InvitationMediaGridView(
        uiState.mediaItems,
        onItemClick = {}
    )
}
