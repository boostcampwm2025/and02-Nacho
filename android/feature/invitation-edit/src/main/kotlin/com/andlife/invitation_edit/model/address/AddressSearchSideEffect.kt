package com.andlife.invitation_edit.model.address

import com.andlife.ui.base.BaseSideEffect

sealed interface AddressSearchSideEffect : BaseSideEffect {
    data class NavigateBackWithAddress(
        val addressUiModel: AddressUiModel,
    ) : AddressSearchSideEffect

    data object NavigateBack : AddressSearchSideEffect
}
