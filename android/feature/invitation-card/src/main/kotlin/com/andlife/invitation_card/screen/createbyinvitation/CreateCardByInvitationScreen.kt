package com.andlife.invitation_card.screen.createbyinvitation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.andlife.editor.screen.EditorScreen
import com.andlife.invitation_card.R
import com.andlife.invitation_card.model.creaetbyinvitation.CreateByInvitationSideEffect
import com.andlife.invitation_card.model.creaetbyinvitation.CreateByInvitationUiEvent
import com.andlife.invitation_card.model.creaetbyinvitation.CreateByInvitationUiState
import com.andlife.invitation_card.viewmodel.CreateByInvitationViewModel
import com.andlife.ui.util.collectWithLifecycle

@Composable
fun CreateCardByInvitationRoute(
    onBackClick: () -> Unit,
    onSuccessCreateCard: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateByInvitationViewModel = hiltViewModel()
) {
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

    CreateCardByInvitationScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
        modifier = modifier
    )
}

@Composable
private fun CreateCardByInvitationScreen(
    uiState: CreateByInvitationUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (CreateByInvitationUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        EditorScreen(
            state = uiState.editorState,
            snackbarHostState = snackbarHostState,
            titleText = stringResource(R.string.txt_create_card),
            modifier = Modifier,
            onBackClick = { onEvent(CreateByInvitationUiEvent.ClickBack) },
            onSaveChangesClick = { onEvent(CreateByInvitationUiEvent.ClickSave) },
            isLoading = uiState.isLoading
        )
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}


