package com.andlife.myinvitation.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.andlife.domain.model.Address
import com.andlife.myinvitation.model.AddressSearchSideEffect
import com.andlife.myinvitation.model.AddressSearchUiEvent
import com.andlife.myinvitation.model.AddressSearchUiState
import com.andlife.ui.base.BaseViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class AddressSearchViewModel : BaseViewModel<AddressSearchUiState, AddressSearchUiEvent, AddressSearchSideEffect>(
    initialState = AddressSearchUiState()
) {

    override val uiState: StateFlow<AddressSearchUiState> = mutableUiState
        .onStart {
            // TODO: loadData()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AddressSearchUiState()
        )

    @OptIn(FlowPreview::class)
    private val searchParamsFlow = uiState
        .map { it.query }
        .distinctUntilChanged()
        .debounce(300L)

    @OptIn(ExperimentalCoroutinesApi::class)
    val addresses = searchParamsFlow
        .flatMapLatest { query ->
            if (query.isBlank()) {
                flowOf(PagingData.empty<Address>())
            } else {
                flowOf(PagingData.empty<Address>()) // TODO: 실제 API 호출
            }
        }
        .cachedIn(viewModelScope)

    override fun onEvent(event: AddressSearchUiEvent) {
        when (event) {
            is AddressSearchUiEvent.UpdateQuery -> updateQuery(event)
            is AddressSearchUiEvent.SelectAddress -> selectAddress(event)
            is AddressSearchUiEvent.ClickClose -> clickClose()
        }
    }

    private fun updateQuery(event: AddressSearchUiEvent.UpdateQuery) {
        updateState { copy(query = event.query) }
    }

    private fun selectAddress(event: AddressSearchUiEvent.SelectAddress) {
        sendEffect(AddressSearchSideEffect.NavigateBackWithAddress(event.address))
    }

    private fun clickClose() {
        sendEffect(AddressSearchSideEffect.NavigateBack)
    }
}
