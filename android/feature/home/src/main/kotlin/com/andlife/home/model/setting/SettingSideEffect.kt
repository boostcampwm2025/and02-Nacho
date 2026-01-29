package com.andlife.home.model.setting

import com.andlife.ui.base.BaseSideEffect

sealed interface SettingSideEffect : BaseSideEffect {
    data object NavigateToLogin : SettingSideEffect
    data object PopBackStack : SettingSideEffect
}
