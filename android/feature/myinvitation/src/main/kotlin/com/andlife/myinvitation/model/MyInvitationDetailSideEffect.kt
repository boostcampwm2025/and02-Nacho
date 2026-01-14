package com.andlife.myinvitation.model

import com.andlife.ui.base.BaseSideEffect

sealed interface MyInvitationDetailSideEffect : BaseSideEffect {
    data object NavigateBack : MyInvitationDetailSideEffect
}
