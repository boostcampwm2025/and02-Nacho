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

    data class NavigateToCreateThanksCard(
        val myInvitationId: Long,
    ) : MyInvitationDetailSideEffect

    data object ThanksCardOnBoarding : MyInvitationDetailSideEffect

    data object ShowMapErrorSnackbar : MyInvitationDetailSideEffect

    data object LinkCopied : MyInvitationDetailSideEffect

    data object SuccessRemoveThanksCard : MyInvitationDetailSideEffect

    data object FailRemoveThanksCard : MyInvitationDetailSideEffect

    data class NavigateToUpdateThanksCard(
        val cardId: Long
    ) : MyInvitationDetailSideEffect
}
