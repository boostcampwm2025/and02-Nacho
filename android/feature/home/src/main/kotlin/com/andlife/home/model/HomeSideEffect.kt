package com.andlife.home.model

import com.andlife.ui.base.BaseSideEffect

sealed interface HomeSideEffect : BaseSideEffect {
    data class ShowMessage(
        val message: String,
    ) : HomeSideEffect

    data class NavigateToInvitationDetail(
        val invitationId: Long,
    ) : HomeSideEffect

    data object NavigateToSetting : HomeSideEffect

    data object NavigateToCreate : HomeSideEffect

    data object RefreshGuestBook : HomeSideEffect

    data object RefreshUpcomingInvitation : HomeSideEffect
}
