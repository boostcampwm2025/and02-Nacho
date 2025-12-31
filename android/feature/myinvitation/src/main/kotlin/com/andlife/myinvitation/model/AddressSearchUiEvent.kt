package com.andlife.myinvitation.model

import com.andlife.ui.base.BaseUiEvent
import com.andlife.ui.model.AddressUiModel

sealed interface AddressSearchUiEvent : BaseUiEvent {
    data class UpdateQuery(val query: String) : AddressSearchUiEvent
    data class SelectAddress(val addressUiModel: AddressUiModel) : AddressSearchUiEvent
    data object ClickBack : AddressSearchUiEvent
}
