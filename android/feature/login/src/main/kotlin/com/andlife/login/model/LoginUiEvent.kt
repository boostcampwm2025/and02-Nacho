package com.andlife.login.model

import com.andlife.ui.base.BaseUiEvent

sealed interface LoginUiEvent : BaseUiEvent {
    data class SocialLoginSuccess(val accessToken: String) : LoginUiEvent
    data object GuestLogin : LoginUiEvent
    data object TestLogin : LoginUiEvent
}
