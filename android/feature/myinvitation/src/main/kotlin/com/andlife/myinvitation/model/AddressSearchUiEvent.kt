package com.andlife.myinvitation.model

import com.andlife.domain.model.Address
import com.andlife.ui.base.BaseUiEvent

sealed interface AddressSearchUiEvent : BaseUiEvent {
    data class UpdateQuery(val query: String) : AddressSearchUiEvent
    data class SelectAddress(val address: Address) : AddressSearchUiEvent
    data object ClickClose : AddressSearchUiEvent
}
