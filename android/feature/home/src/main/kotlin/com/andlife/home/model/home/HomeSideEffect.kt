package com.andlife.home.model.home

import com.andlife.ui.base.BaseSideEffect

sealed interface HomeSideEffect : BaseSideEffect {
    data class ShowMessage(
        val message: String,
    ) : HomeSideEffect

    data class NavigateToInvitationDetail(
        val invitationId: Long,
    ) : HomeSideEffect

    data class NavigateToMyInvitationDetail(
        val invitationId: Long,
    ) : HomeSideEffect

    data object NavigateToSetting : HomeSideEffect

    data object NavigateToCreate : HomeSideEffect

    data object ScrollToTop : HomeSideEffect

    data object RefreshFailure : HomeSideEffect
    data object NeedRefresh : HomeSideEffect

    data object ReportSuccess : HomeSideEffect
    data class ReportFailure(val message: String? = null) : HomeSideEffect
}
