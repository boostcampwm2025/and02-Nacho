package com.andlife.ui.base

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Stable interface BaseUiState
interface BaseUiEvent
interface BaseSideEffect

abstract class BaseViewModel<State : BaseUiState, Event : BaseUiEvent, Effect : BaseSideEffect>(
    initialState: State
) : ViewModel() {

    protected val mutableUiState: MutableStateFlow<State> = MutableStateFlow(initialState)
    abstract val uiState: StateFlow<State>

    private val _effectChannel = Channel<Effect>(Channel.BUFFERED)
    val effectFlow = _effectChannel.receiveAsFlow()

    protected fun updateState(reducer: State.() -> State) {
        mutableUiState.update { it.reducer() }
    }

    abstract fun onEvent(event: Event)

    protected fun sendEffect(effect: Effect) {
        viewModelScope.launch {
            _effectChannel.send(effect)
        }
    }
}