package com.andlife.invitation_card.screen.createcard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.andlife.editor.screen.EditorScreen
import com.andlife.editor.state.EditorState
import com.andlife.invitation_card.R
import com.andlife.invitation_card.viewmodel.CreateCardViewModel

@Composable
fun CreateCardRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateCardViewModel = hiltViewModel()
) {
    val state = remember {
        viewModel.getState()
    }

    LaunchedEffect(Unit) {
        viewModel.loadEditable()
    }

    CreateCardScreen(
        state = state,
        onBackClick = onBackClick,
        onSaveComplete = {
            viewModel.saveCard()
            onBackClick()
        },
        modifier = modifier
    )
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
