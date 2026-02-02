package com.andlife.myinvitation.model.detail

import android.text.Editable
import com.andlife.model.invitation.InvitationContentsUiModel
import com.andlife.ui.base.BaseUiState

data class MyInvitationDetailUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val invitationContentsUiModel: InvitationContentsUiModel = InvitationContentsUiModel(),
    val cachedCardEditable: Editable? = null,
    val thanksCardEditableCache: Editable? = null,
    val editCardEnabled: Boolean = false,
) : BaseUiState {
    val hasThanksCard = invitationContentsUiModel.thanksCard != null
}
