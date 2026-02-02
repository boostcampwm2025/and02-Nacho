package com.andlife.invitation_card.screen.createbyinvitation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import com.andlife.editor.screen.EditorScreen
import com.andlife.invitation_card.R
import com.andlife.ui.component.dialog.NachoInfoDialog
import com.andlife.invitation_card.model.creaetbyinvitation.CreateByInvitationSideEffect
import com.andlife.invitation_card.model.creaetbyinvitation.CreateByInvitationUiEvent
import com.andlife.invitation_card.model.creaetbyinvitation.CreateByInvitationUiState
import com.andlife.invitation_card.viewmodel.CreateByInvitationViewModel
import com.andlife.ui.component.loading.InvitationLoadingIndicator
import com.andlife.ui.util.collectWithLifecycle

@Composable
fun CreateCardByInvitationRoute(
    onBackClick: () -> Unit,
    onSuccessCreateCard: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateByInvitationViewModel = hiltViewModel()
) {
    var showBackDialog by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val res = LocalResources.current

    viewModel.effectFlow.collectWithLifecycle { effect ->
        when (effect) {
            CreateByInvitationSideEffect.FailCreateCard -> {
                snackbarHostState.showSnackbar(res.getString(R.string.snackbar_fail_create_card))
            }

            CreateByInvitationSideEffect.NavigateBack -> {
                onBackClick()
            }

            CreateByInvitationSideEffect.SuccessCreateCard -> {
                onSuccessCreateCard()
            }
        }
    }

    BackHandler {
        showBackDialog = true
    }

    CreateCardByInvitationScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onBackClick = { showBackDialog = true },
        modifier = modifier,
        snackbarHostState = snackbarHostState,
    )

    if (showBackDialog) {
        NachoInfoDialog(
            title = stringResource(R.string.txt_exit_dialog_title),
            message = stringResource(R.string.txt_exit_dialog_message),
            confirmText = stringResource(R.string.btn_exit),
            dismissText = stringResource(R.string.btn_continue),
            onConfirm = {
                viewModel.onEvent(CreateByInvitationUiEvent.ClickBack)
                showBackDialog = false
            },
            onDismiss = { showBackDialog = false },
        )
    }
}

@Composable
private fun CreateCardByInvitationScreen(
    uiState: CreateByInvitationUiState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onEvent: (CreateByInvitationUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        EditorScreen(
            state = uiState.editorState,
            snackbarHostState = snackbarHostState,
            titleText = stringResource(R.string.txt_create_card),
            modifier = Modifier,
            onBackClick = onBackClick,
            onSaveChangesClick = { onEvent(CreateByInvitationUiEvent.ClickSave) },
            isLoading = uiState.isLoading
        )
        if (uiState.isLoading) {
            InvitationLoadingIndicator()
        }
    }
}


