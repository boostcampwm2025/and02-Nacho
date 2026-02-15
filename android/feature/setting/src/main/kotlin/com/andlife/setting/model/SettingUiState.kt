package com.andlife.setting.model

import com.andlife.domain.model.auth.AuthState
import com.andlife.ui.base.BaseUiState

data class SettingUiState(
    val isLoading: Boolean = true,
    val isOverlayLoading: Boolean = false,
    val authState: AuthState = AuthState.Loading,
    val nicknameInput: String = "",
    val nicknameError: NicknameError = NicknameError.NONE,
) : BaseUiState {
    val isNicknameValid: Boolean =
        nicknameInput.isNotBlank() && nicknameError == NicknameError.NONE
}

enum class NicknameError {
    EMPTY,
    TOO_LONG,
    INVALID_CHAR,
    NONE
}
