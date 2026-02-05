package com.andlife.thanks_card.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andlife.editor.screen.EditorScreen
import com.andlife.editor.state.EditorState
import com.andlife.thanks_card.R
import com.andlife.thanks_card.model.update.UpdateThanksCardSideEffect
import com.andlife.thanks_card.model.update.UpdateThanksCardUiEvent
import com.andlife.thanks_card.model.update.UpdateThanksCardUiState
import com.andlife.thanks_card.viewmodel.UpdateThanksCardViewModel
import com.andlife.ui.component.dialog.NachoInfoDialog
import com.andlife.ui.component.loading.InvitationLoadingError
import com.andlife.ui.component.loading.InvitationLoadingIndicator
import com.andlife.ui.util.collectWithLifecycle
import kotlinx.coroutines.launch

@Composable
fun UpdateThanksCardRoute(
    onSuccessfulUpdate: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UpdateThanksCardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val editorState = remember { viewModel.getEditorState() }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val res = LocalResources.current
    var showBackDialog by remember { mutableStateOf(false) }

    viewModel.effectFlow.collectWithLifecycle { effect ->
        when (effect) {
            UpdateThanksCardSideEffect.OnBack -> {
                if (editorState.currentText.isNotEmpty()) {
                    showBackDialog = true
                } else {
                    onBackClick()
                }
            }

            UpdateThanksCardSideEffect.OnFailUpdateThanksCard -> {
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(res.getString(R.string.txt_fail_update_thanks_card))
                }
            }

            UpdateThanksCardSideEffect.OnSuccessUpdateThanksCard -> {
                onSuccessfulUpdate()
            }
        }
    }

    BackHandler {
        showBackDialog = true
    }

    UpdateThanksCardScreen(
        editorState = editorState,
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
        modifier = modifier
    )
    if (showBackDialog) {
        NachoInfoDialog(
            title = stringResource(R.string.txt_exit_dialog_title),
            message = stringResource(R.string.txt_exit_dialog_message),
            confirmText = stringResource(R.string.btn_exit),
            dismissText = stringResource(R.string.btn_continue),
            onConfirm = {
                showBackDialog = false
                onBackClick()
            },
            onDismiss = { showBackDialog = false },
        )
    }
}

@Composable
fun UpdateThanksCardScreen(
    editorState: EditorState,
    uiState: UpdateThanksCardUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (UpdateThanksCardUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (uiState.isError) {
        InvitationLoadingError(
            modifier = modifier,
            onRetry = { onEvent(UpdateThanksCardUiEvent.OnClickRetry) }
        )
        return
    }

    Box(modifier = modifier) {
        EditorScreen(
            state = editorState,
            snackbarHostState = snackbarHostState,
            titleText = stringResource(R.string.txt_update_thanks_card),
            onBackClick = { onEvent(UpdateThanksCardUiEvent.OnClickBack) },
            onSaveChangesClick = { onEvent(UpdateThanksCardUiEvent.OnClickSaveChanges) },
            isLoading = uiState.isLoading,
        )
        if (uiState.isLoading) {
            InvitationLoadingIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
