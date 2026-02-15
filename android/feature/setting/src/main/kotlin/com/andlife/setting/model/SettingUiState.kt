package com.andlife.setting.model

import com.andlife.domain.model.auth.AuthState
import com.andlife.ui.base.BaseUiState

data class SettingUiState(
    val isLoading: Boolean = true,
    val isOverlayLoading: Boolean = false,
    val authState: AuthState = AuthState.Loading
) : BaseUiState
