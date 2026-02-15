package com.andlife.setting.model

import com.andlife.ui.base.BaseSideEffect

sealed interface SettingSideEffect : BaseSideEffect {
    data object NavigateToLogin : SettingSideEffect
    data object PopBackStack : SettingSideEffect
    data object FailSignOut : SettingSideEffect
    data object SuccessSignOutWithServer : SettingSideEffect

}
