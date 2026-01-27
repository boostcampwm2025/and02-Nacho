package com.andlife.invitation_card.screen.updateacard

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andlife.designsystem.component.dialog.NachoDialog
import com.andlife.editor.screen.EditorScreen
import com.andlife.invitation_card.R
import com.andlife.invitation_card.component.BackDialogContent
import com.andlife.invitation_card.model.updatecard.UpdateCardSideEffect
import com.andlife.invitation_card.model.updatecard.UpdateCardUiEvent
import com.andlife.invitation_card.model.updatecard.UpdateCardUiState
import com.andlife.invitation_card.viewmodel.UpdateCardViewModel
import com.andlife.ui.component.loading.InvitationLoadingIndicator
import com.andlife.ui.util.collectWithLifecycle

@Composable
fun UpdateCardRoute(
    onSuccessfulUpdate: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UpdateCardViewModel = hiltViewModel()
) {
    var showBackDialog by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val res = LocalResources.current

    viewModel.effectFlow.collectWithLifecycle { effect ->
        when (effect) {
            UpdateCardSideEffect.FailUpdateCard -> {
                snackbarHostState.showSnackbar(res.getString(R.string.snackbar_fila_update_card))
            }

            UpdateCardSideEffect.OnBackNavigation -> {
                onBackClick()
            }

            UpdateCardSideEffect.SuccessUpdateCard -> {
                onSuccessfulUpdate()
            }
        }
    }

    BackHandler {
        showBackDialog = true
    }

    UpdateCardScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onBackClick = { showBackDialog = true },
        modifier = modifier,
        snackbarHostState = snackbarHostState,
    )

    if (showBackDialog) {
        NachoDialog(onDismiss = { showBackDialog = false }) {
            BackDialogContent(
                onConfirm = {
                    viewModel.onEvent(UpdateCardUiEvent.OnClickBackNavigation)
                    showBackDialog = false
                },
                onDismiss = { showBackDialog = false }
            )
        }
    }
}

@Composable
fun UpdateCardScreen(
    uiState: UpdateCardUiState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onEvent: (UpdateCardUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        EditorScreen(
            state = uiState.editorState,
            titleText = stringResource(R.string.txt_update_card),
            snackbarHostState = snackbarHostState,
            onBackClick = onBackClick,
            onSaveChangesClick = { onEvent(UpdateCardUiEvent.OnClickUpdateCard) },
            isLoading = uiState.isLoading,
            modifier = Modifier,
        )
        if (uiState.isLoading) {
            InvitationLoadingIndicator()
        }
    }
}
