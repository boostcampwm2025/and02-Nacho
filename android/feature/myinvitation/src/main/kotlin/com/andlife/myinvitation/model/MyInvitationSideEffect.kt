package com.andlife.myinvitation.model

import com.andlife.ui.base.BaseSideEffect

sealed interface MyInvitationSideEffect : BaseSideEffect {
    data class NavigateToDetail(val id: Long) : MyInvitationSideEffect
    object RefreshFailure : MyInvitationSideEffect
}
