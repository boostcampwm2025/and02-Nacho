package com.andlife.setting.viewmodel

import androidx.lifecycle.viewModelScope
import com.andlife.domain.repository.user.UserRepository
import com.andlife.domain.util.onFailure
import com.andlife.domain.util.onSuccess
import com.andlife.setting.model.NicknameError
import com.andlife.setting.model.SettingSideEffect
import com.andlife.setting.model.SettingUiEvent
import com.andlife.setting.model.SettingUiState
import com.andlife.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val userRepository: UserRepository
) : BaseViewModel<SettingUiState, SettingUiEvent, SettingSideEffect>(SettingUiState()) {

    override val uiState: StateFlow<SettingUiState> = mutableUiState
        .onStart {
            loadUserInfo()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.Lazily,
            initialValue = SettingUiState()
        )

    override fun onEvent(event: SettingUiEvent) {
        when (event) {
            SettingUiEvent.ClickBack -> sendEffect(SettingSideEffect.PopBackStack)
            SettingUiEvent.ClickLogout -> logout()
            SettingUiEvent.ClickSignOut -> signOut()
        }
    }

    private fun validateNickname(name:String) {
        val specialChars = Regex("[^a-zA-Z0-9가-힣]")
        val error = when {
            name.isBlank() -> NicknameError.EMPTY
            name.length > 12 -> NicknameError.TOO_LONG
            specialChars.containsMatchIn(name) -> NicknameError.INVALID_CHAR
            else -> NicknameError.NONE
        }
        updateState {
            copy(
                nicknameInput = name,
                nicknameError = error
            )
        }
    }

    private fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            sendEffect(SettingSideEffect.NavigateToLogin)
        }
    }

    private fun signOut() {
        viewModelScope.launch {
            updateState { copy(isOverlayLoading = true) }
            userRepository.signOut()
                .onSuccess {
                    sendEffect(SettingSideEffect.SuccessSignOutWithServer)
                }
                .onFailure { error, msg ->
                    sendEffect(SettingSideEffect.FailSignOut)
                }
            updateState { copy(isOverlayLoading = false) }
        }
    }


    private fun loadUserInfo() {
        viewModelScope.launch {
            val userState = userRepository.getUserInfo()
            updateState { copy(isLoading = false, authState = userState) }
        }
    }
}
