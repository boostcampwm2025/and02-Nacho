package com.andlife.home.viewmodel

import androidx.lifecycle.viewModelScope
import com.andlife.domain.repository.user.UserRepository
import com.andlife.home.model.setting.SettingSideEffect
import com.andlife.home.model.setting.SettingUiEvent
import com.andlife.home.model.setting.SettingUiState
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
            started = SharingStarted.Lazily,
            initialValue = SettingUiState()
        )

    override fun onEvent(event: SettingUiEvent) {
        when (event) {
            SettingUiEvent.ClickBack -> sendEffect(SettingSideEffect.PopBackStack)
            SettingUiEvent.ClickLogout -> logout()
        }
    }

    private fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            sendEffect(SettingSideEffect.NavigateToLogin)
        }
    }

    private fun loadUserInfo() {
        viewModelScope.launch {
            val userState = userRepository.getUserInfo()
            updateState { copy(isLoading = false, authState = userState) }
        }
    }
}
