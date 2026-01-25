package com.andlife.invitation_card.model.updatecard

import com.andlife.editor.state.EditorState
import com.andlife.ui.base.BaseUiState

data class UpdateCardUiState(
    val editorState: EditorState,
    val isLoading: Boolean = false
) : BaseUiState
