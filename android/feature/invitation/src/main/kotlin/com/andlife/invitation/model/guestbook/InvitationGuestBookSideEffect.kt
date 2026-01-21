package com.andlife.invitation.model.guestbook

import com.andlife.ui.base.BaseSideEffect

sealed interface InvitationGuestBookSideEffect : BaseSideEffect {
    data class ShowSnackbar(
        val message: String,
    ) : InvitationGuestBookSideEffect

    data object CreateGuestBookSuccess : InvitationGuestBookSideEffect
}
