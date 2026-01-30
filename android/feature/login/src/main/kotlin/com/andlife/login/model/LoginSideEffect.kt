package com.andlife.login.model

import com.andlife.ui.base.BaseSideEffect

sealed interface LoginSideEffect : BaseSideEffect {
    data object FailSocialLogin : LoginSideEffect
    data object FailGuestLogin : LoginSideEffect
}
