package com.andlife.invitation.model.guestbook

import com.andlife.domain.model.auth.AuthState
import com.andlife.ui.base.BaseSideEffect

sealed interface InvitationGuestBookSideEffect : BaseSideEffect {
    data class ShowSnackbar(
        val message: String,
    ) : InvitationGuestBookSideEffect

    data object CreateGuestBookSuccess : InvitationGuestBookSideEffect

    data object UpdateGuestBookSuccess : InvitationGuestBookSideEffect

    data object DeleteGuestBookSuccess : InvitationGuestBookSideEffect

    data object LaunchCamera : InvitationGuestBookSideEffect

    data object StartAudioRecording : InvitationGuestBookSideEffect

    data object StopAudioRecording : InvitationGuestBookSideEffect

    data object ScrollToTop : InvitationGuestBookSideEffect

    data object RefreshFailure : InvitationGuestBookSideEffect

    data class AuthStateChanged(
        val authState: AuthState
    ) : InvitationGuestBookSideEffect
}
