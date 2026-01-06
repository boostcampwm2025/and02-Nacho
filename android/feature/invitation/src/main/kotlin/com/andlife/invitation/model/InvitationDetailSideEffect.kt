package com.andlife.invitation.model

import com.andlife.ui.base.BaseSideEffect

sealed interface InvitationDetailSideEffect : BaseSideEffect {
    data object NavigateBack : InvitationDetailSideEffect
}
