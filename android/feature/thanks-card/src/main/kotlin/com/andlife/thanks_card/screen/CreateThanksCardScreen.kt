package com.andlife.thanks_card.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
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
import com.andlife.designsystem.component.dialog.NachoDialog
import com.andlife.editor.screen.EditorScreen
import com.andlife.editor.state.EditorState
import com.andlife.thanks_card.R
import com.andlife.thanks_card.model.create.CreateThanksSideEffect
import com.andlife.thanks_card.model.create.CreateThanksUiEvent
import com.andlife.thanks_card.model.create.CreateThanksUiState
import com.andlife.thanks_card.viewmodel.CreateThanksCardViewModel
import com.andlife.ui.component.card.DiscardChangesDialogContent
import com.andlife.ui.component.loading.InvitationLoadingIndicator
import com.andlife.ui.util.collectWithLifecycle
import kotlinx.coroutines.launch

@Composable
fun ThanksCardRoute(
    onSuccess: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateThanksCardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val editor = viewModel.getEditorState()
    var showBackDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val res = LocalResources.current

    viewModel.effectFlow.collectWithLifecycle { effect ->
        when (effect) {
            CreateThanksSideEffect.FailCreateThanksCard -> {
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(res.getString(R.string.txt_fail_create_thanks_card))
                }
            }

            CreateThanksSideEffect.SuccessCreateThanksCard -> {
                onSuccess()
            }

            CreateThanksSideEffect.OnBack -> {
                if (editor.currentText.isNotEmpty()) {
                    showBackDialog = true
                } else {
                    onBackClick()
                }
            }
        }
    }

    ThanksCardScreen(
        uiState = uiState,
        editor = editor,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
        modifier = modifier
    )

    if (showBackDialog) {
        NachoDialog(onDismiss = { showBackDialog = false }) {
            DiscardChangesDialogContent(
                onConfirm = {
                    showBackDialog = false
                    onBackClick()
                },
                onDismiss = { showBackDialog = false }
            )
        }
    }
}

@Composable
fun ThanksCardScreen(
    uiState: CreateThanksUiState,
    editor: EditorState,
    snackbarHostState: SnackbarHostState,
    onEvent: (CreateThanksUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box {
        EditorScreen(
            modifier = modifier,
            state = editor,
            snackbarHostState = snackbarHostState,
            titleText = stringResource(R.string.txt_create_thanks_card),
            onBackClick = { onEvent(CreateThanksUiEvent.OnClickBack) },
            onSaveChangesClick = { onEvent(CreateThanksUiEvent.OnClickCreate) },
            isLoading = uiState.isLoading
        )
        if (uiState.isLoading) {
            InvitationLoadingIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
