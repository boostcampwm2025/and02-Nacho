package com.andlife.setting.model

import com.andlife.ui.base.BaseUiEvent

sealed interface SettingUiEvent : BaseUiEvent {
    data object ClickLogout : SettingUiEvent
    data object ClickBack : SettingUiEvent
    data object ClickSignOut : SettingUiEvent
    data class OnNicknameChanged(val nickname: String) : SettingUiEvent
    data class ClickConfirmNickname(val nickname: String) : SettingUiEvent
}
