package com.andlife.myinvitation.model

import com.andlife.ui.base.BaseSideEffect
import com.andlife.ui.model.AddressUiModel

sealed interface AddressSearchSideEffect : BaseSideEffect {
    data class NavigateBackWithAddress(val addressUiModel: AddressUiModel) : AddressSearchSideEffect
    data object NavigateBack : AddressSearchSideEffect
}
