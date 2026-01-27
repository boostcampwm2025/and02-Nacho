package com.andlife.invitation_edit.model.form

import com.andlife.ui.base.BaseUiState

data class InvitationFormUiState(
    val invitationId: Long? = null,
    val cardId: Long? = null,
    val invitationFormUiModel: InvitationFormUiModel = InvitationFormUiModel(),
    val isLoading: Boolean = false,
) : BaseUiState {
    val isValid: Boolean
        get() = invitationFormUiModel.isValid
}
