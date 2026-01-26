package com.andlife.login.viewmodel

import com.andlife.login.model.LoginSideEffect
import com.andlife.login.model.LoginUiEvent
import com.andlife.login.model.LoginUiState
import com.andlife.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(

) : BaseViewModel<LoginUiState, LoginUiEvent, LoginSideEffect>(LoginUiState()) {


    override val uiState: StateFlow<LoginUiState> = mutableUiState.asStateFlow()
    override fun onEvent(event: LoginUiEvent) {
        TODO("Not yet implemented")
    }
}
