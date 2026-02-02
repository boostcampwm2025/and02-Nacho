package com.andlife.home.model.setting

import com.andlife.ui.base.BaseUiEvent

sealed interface SettingUiEvent : BaseUiEvent {
    data object ClickLogout : SettingUiEvent
    data object ClickBack : SettingUiEvent
}
