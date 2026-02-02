package com.andlife.invitation_card.screen.createcard

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.andlife.designsystem.component.dialog.NachoDialog
import com.andlife.editor.screen.EditorScreen
import com.andlife.editor.state.EditorState
import com.andlife.invitation_card.R
import com.andlife.invitation_card.viewmodel.CreateCardViewModel
import com.andlife.ui.component.card.DiscardChangesDialogContent

@Composable
fun CreateCardRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateCardViewModel = hiltViewModel()
) {
    var showBackDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = true) {
        showBackDialog = true
    }

    val state = remember { viewModel.getState() }

    LaunchedEffect(Unit) {
        viewModel.loadEditable()
    }

    CreateCardScreen(
        state = state,
        onBackClick = { showBackDialog = true },
        onSaveComplete = {
            viewModel.saveCard()
            onBackClick()
        },
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
private fun CreateCardScreen(
    state: EditorState,
    onBackClick: () -> Unit,
    onSaveComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    EditorScreen(
        state = state,
        titleText = stringResource(R.string.txt_create_card),
        onBackClick = onBackClick,
        onSaveChangesClick = onSaveComplete,
        modifier = modifier,
    )
}
