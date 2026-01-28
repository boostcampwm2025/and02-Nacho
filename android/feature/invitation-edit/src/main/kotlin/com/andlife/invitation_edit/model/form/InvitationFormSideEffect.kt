package com.andlife.invitation_edit.model.form

import com.andlife.ui.base.BaseSideEffect

sealed interface InvitationFormSideEffect : BaseSideEffect {
    data object FullImage : InvitationFormSideEffect
    data object OnBack : InvitationFormSideEffect
    data object FailLoad : InvitationFormSideEffect
    data object FailSave : InvitationFormSideEffect
    data class SuccessSave(val id: Long) : InvitationFormSideEffect
    data object InvalidTime : InvitationFormSideEffect
}
