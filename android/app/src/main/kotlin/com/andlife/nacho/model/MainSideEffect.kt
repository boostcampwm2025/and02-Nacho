package com.andlife.nacho.model

import com.andlife.ui.base.BaseSideEffect

sealed interface MainSideEffect : BaseSideEffect {
    data class NavigateToDetail(
        val invitationId: Long,
        val isFromDeepLink: Boolean = true,
    ) : MainSideEffect

    data object NavigateToLogin : MainSideEffect

    data object NavigateToHome : MainSideEffect
}
