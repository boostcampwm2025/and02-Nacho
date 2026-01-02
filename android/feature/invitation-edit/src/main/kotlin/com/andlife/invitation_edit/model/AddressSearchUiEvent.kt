package com.andlife.invitation_edit.model

import com.andlife.ui.base.BaseUiEvent

sealed interface AddressSearchUiEvent : BaseUiEvent {
    data class UpdateQuery(
        val query: String,
    ) : AddressSearchUiEvent

    data class SelectAddress(
        val addressUiModel: AddressUiModel,
    ) : AddressSearchUiEvent

    data object ClickBack : AddressSearchUiEvent
}
