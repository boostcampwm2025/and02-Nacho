package com.andlife.invitation.screen.guestbook

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andlife.invitation.component.InvitationMediaGridView
import com.andlife.invitation.model.guestbook.collection.InvitationCollectionUiEvent
import com.andlife.invitation.viewmodel.InvitationCollectionViewModel

@Composable
fun InvitationCollectionScreen(
    modifier: Modifier = Modifier,
    viewModel: InvitationCollectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    InvitationMediaGridView(
        items = uiState.mediaItems,
        onItemClick = { index ->
            viewModel.onEvent(InvitationCollectionUiEvent.OpenStory(index))
        }
    )

    if (uiState.isDetailMode) {
        Dialog(
            onDismissRequest = { viewModel.onEvent(InvitationCollectionUiEvent.CloseStory) },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            InvitationStoryScreen(
                viewModel = viewModel,
                initialIndex = uiState.selectedIndex,
                onClose = { viewModel.onEvent(InvitationCollectionUiEvent.CloseStory) }
            )
        }
    }
}
