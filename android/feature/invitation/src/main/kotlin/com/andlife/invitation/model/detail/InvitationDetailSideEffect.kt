package com.andlife.invitation.model.detail

import com.andlife.ui.base.BaseSideEffect

sealed interface InvitationDetailSideEffect : BaseSideEffect {
    data object NavigateBack : InvitationDetailSideEffect
}
