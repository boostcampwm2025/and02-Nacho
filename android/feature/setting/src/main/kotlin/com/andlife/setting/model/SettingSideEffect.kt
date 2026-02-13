package com.andlife.setting.model

import com.andlife.ui.base.BaseSideEffect

sealed interface SettingSideEffect : BaseSideEffect {
    data object NavigateToLogin : SettingSideEffect
    data object PopBackStack : SettingSideEffect
    data object FailSignOut : SettingSideEffect
    data object SuccessSignOutWithServer : SettingSideEffect
    data class ShowSnackbar(val messageType: SettingMessage) : SettingSideEffect
}

enum class SettingMessage {
    PROFILE_UPDATE_SUCCESS,
    PROFILE_UPDATE_FAIL,
    NICKNAME_INVALID
}
