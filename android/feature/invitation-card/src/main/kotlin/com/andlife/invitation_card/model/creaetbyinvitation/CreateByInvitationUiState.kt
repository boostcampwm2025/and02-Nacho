package com.andlife.invitation_card.model.creaetbyinvitation

import com.andlife.editor.state.EditorState
import com.andlife.ui.base.BaseUiState

data class CreateByInvitationUiState(
    val editorState: EditorState,
    val isLoading: Boolean = false
) : BaseUiState
