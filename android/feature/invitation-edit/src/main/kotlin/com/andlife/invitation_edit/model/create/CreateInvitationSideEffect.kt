package com.andlife.invitation_edit.model.create

import com.andlife.ui.base.BaseSideEffect

sealed interface CreateInvitationSideEffect : BaseSideEffect {
    data object FullImage : CreateInvitationSideEffect
}
