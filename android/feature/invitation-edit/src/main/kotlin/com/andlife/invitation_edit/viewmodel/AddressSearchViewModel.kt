package com.andlife.invitation_edit.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.andlife.domain.repository.AddressRepository
import com.andlife.invitation_edit.model.AddressSearchSideEffect
import com.andlife.invitation_edit.model.AddressSearchUiEvent
import com.andlife.invitation_edit.model.AddressSearchUiState
import com.andlife.invitation_edit.model.toUiModel
import com.andlife.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class AddressSearchViewModel
    @Inject
    constructor(
        private val addressRepository: AddressRepository,
    ) : BaseViewModel<AddressSearchUiState, AddressSearchUiEvent, AddressSearchSideEffect>(
            initialState = AddressSearchUiState(),
        ) {
        override val uiState: StateFlow<AddressSearchUiState> = mutableUiState.asStateFlow()

        @OptIn(FlowPreview::class)
        private val searchParamsFlow =
            uiState
                .map { it.query }
                .distinctUntilChanged()
                .debounce(300L)

        @OptIn(ExperimentalCoroutinesApi::class)
        val addresses =
            searchParamsFlow
                .flatMapLatest { query ->
                    if (query.isBlank()) {
                        flowOf(PagingData.empty())
                    } else {
                        addressRepository.searchAddress(query)
                    }
                }.map { pagingData ->
                    pagingData.map { address -> address.toUiModel() }
                }.cachedIn(viewModelScope)

        override fun onEvent(event: AddressSearchUiEvent) {
            when (event) {
                is AddressSearchUiEvent.UpdateQuery -> updateQuery(event)
                is AddressSearchUiEvent.SelectAddress -> selectAddress(event)
                is AddressSearchUiEvent.ClickBack -> clickClose()
            }
        }

        private fun updateQuery(event: AddressSearchUiEvent.UpdateQuery) {
            updateState { copy(query = event.query) }
        }

        private fun selectAddress(event: AddressSearchUiEvent.SelectAddress) {
            sendEffect(AddressSearchSideEffect.NavigateBackWithAddress(event.addressUiModel))
        }

        private fun clickClose() {
            sendEffect(AddressSearchSideEffect.NavigateBack)
        }
    }
