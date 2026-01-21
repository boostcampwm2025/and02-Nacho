package com.andlife.invitation_edit.model.create

import com.andlife.ui.base.BaseSideEffect

sealed interface CreateInvitationSideEffect : BaseSideEffect {
    data object FullImage : CreateInvitationSideEffect
    data object OnBack : CreateInvitationSideEffect
    data object FailCreate : CreateInvitationSideEffect
    data class SuccessCreate(val id: Long) : CreateInvitationSideEffect
}
