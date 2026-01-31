package com.andlife.myinvitation.model

import com.andlife.ui.base.BaseSideEffect

sealed interface MyInvitationSideEffect : BaseSideEffect {
    data object NavigateToCreate : MyInvitationSideEffect
    data class NavigateToDetail(val id: Long) : MyInvitationSideEffect
    data object RefreshFailure : MyInvitationSideEffect
    data object DeleteSuccess : MyInvitationSideEffect
    data object DeleteFailure : MyInvitationSideEffect
}
