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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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

    val query: StateFlow<String> = uiState
        .map { it.query }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val addresses = uiState
        .map { it.query }
        .flatMapLatest { query ->
            // TODO: API 연동 시 SearchAddressUseCase 사용
            // searchAddressUseCase(query)

            // 임시: 빈 데이터 반환
            flowOf(PagingData.empty<Address>())
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
