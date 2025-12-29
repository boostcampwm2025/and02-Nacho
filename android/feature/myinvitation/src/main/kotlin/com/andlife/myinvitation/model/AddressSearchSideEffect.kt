package com.andlife.myinvitation.model

import com.andlife.domain.model.Address
import com.andlife.ui.base.BaseSideEffect

sealed interface AddressSearchSideEffect : BaseSideEffect {
    data class NavigateBackWithAddress(val address: Address) : AddressSearchSideEffect
    data object NavigateBack : AddressSearchSideEffect
}
