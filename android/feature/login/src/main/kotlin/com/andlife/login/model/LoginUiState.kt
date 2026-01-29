package com.andlife.login.model

import com.andlife.ui.base.BaseUiState

data class LoginUiState(
    val isLoading: Boolean = false,
    val fromSplash: Boolean = true,
) : BaseUiState
