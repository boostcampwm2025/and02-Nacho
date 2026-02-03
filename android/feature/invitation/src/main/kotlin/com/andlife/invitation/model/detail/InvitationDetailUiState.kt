package com.andlife.invitation.model.detail

import android.text.Editable
import com.andlife.model.invitation.InvitationContentsUiModel
import com.andlife.ui.base.BaseUiState

data class InvitationDetailUiState(
    val isLoading: Boolean = true,
    val isOverlayLoading: Boolean = false,
    val isError: Boolean = false,
    val invitationContentsUiModel: InvitationContentsUiModel = InvitationContentsUiModel(),
    val editableCache: Editable? = null,
    val thanksCardEditableCache: Editable? = null
) : BaseUiState {
    val hasThanksCard = invitationContentsUiModel.thanksCard != null
}
