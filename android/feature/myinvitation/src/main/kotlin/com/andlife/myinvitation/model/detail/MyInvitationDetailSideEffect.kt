package com.andlife.myinvitation.model.detail

import com.andlife.ui.base.BaseSideEffect
import kotlinx.collections.immutable.ImmutableList

sealed interface MyInvitationDetailSideEffect : BaseSideEffect {
    data object NavigateBack : MyInvitationDetailSideEffect

    data class NavigateToEditCard(
        val cardId: Long,
    ) : MyInvitationDetailSideEffect

    data class NavigateToCreateCard(
        val myInvitationId: Long,
    ) : MyInvitationDetailSideEffect

    data class NavigateToEditInvitation(
        val myInvitationId: Long,
    ) : MyInvitationDetailSideEffect

    data object ShowMapErrorSnackbar : MyInvitationDetailSideEffect
}
