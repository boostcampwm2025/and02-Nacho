package com.andlife.invitation.model

import com.andlife.ui.base.BaseSideEffect

sealed interface InvitationSideEffect : BaseSideEffect {
    data class NavigateToDetail(val id: Long) : InvitationSideEffect
    data object RefreshFailure : InvitationSideEffect
    data object LeaveSuccess : InvitationSideEffect
    data object LeaveFailure : InvitationSideEffect
    data object RefreshFromDeepLink : InvitationSideEffect
}
