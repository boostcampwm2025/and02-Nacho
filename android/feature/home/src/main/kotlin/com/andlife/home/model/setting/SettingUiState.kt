package com.andlife.home.model.setting

import com.andlife.domain.model.auth.AuthState
import com.andlife.ui.base.BaseUiState

data class SettingUiState(
    val isLoading: Boolean = true,
    val authState: AuthState = AuthState.Loading
) : BaseUiState
