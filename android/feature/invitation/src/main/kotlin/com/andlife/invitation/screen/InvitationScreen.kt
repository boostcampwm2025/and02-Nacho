package com.andlife.invitation.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andlife.invitation.viewmodel.InvitationViewModel
import com.andlife.ui.component.media.MediaGridView

@Composable
fun InvitationScreen(
    modifier: Modifier = Modifier,
    viewModel: InvitationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MediaGridView(
        uiState.mediaItems,
        onItemClick = {}
    )
}
