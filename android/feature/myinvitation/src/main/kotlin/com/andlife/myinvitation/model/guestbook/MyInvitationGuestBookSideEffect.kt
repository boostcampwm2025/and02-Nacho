package com.andlife.myinvitation.model.guestbook

import com.andlife.ui.base.BaseSideEffect

sealed interface MyInvitationGuestBookSideEffect : BaseSideEffect {
    data class ShowSnackbar(
        val message: String,
    ) : MyInvitationGuestBookSideEffect

    data object CreateGuestBookSuccess : MyInvitationGuestBookSideEffect

    data object UpdateGuestBookSuccess : MyInvitationGuestBookSideEffect

    data object DeleteGuestBookSuccess : MyInvitationGuestBookSideEffect

    data object ScrollToTop : MyInvitationGuestBookSideEffect

    data object RefreshFailure : MyInvitationGuestBookSideEffect

    data object LaunchCamera : MyInvitationGuestBookSideEffect

    data object StartAudioRecording : MyInvitationGuestBookSideEffect

    data object StopAudioRecording : MyInvitationGuestBookSideEffect
}
