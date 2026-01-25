package com.andlife.invitation_card.model.creaetbyinvitation

import com.andlife.ui.base.BaseSideEffect

sealed interface CreateByInvitationSideEffect : BaseSideEffect {
    data object FailCreateCard : CreateByInvitationSideEffect
    data object SuccessCreateCard : CreateByInvitationSideEffect
    data object NavigateBack : CreateByInvitationSideEffect
}
