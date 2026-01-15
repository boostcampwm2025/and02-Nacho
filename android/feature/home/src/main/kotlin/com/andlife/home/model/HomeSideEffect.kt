package com.andlife.home.model

import com.andlife.ui.base.BaseSideEffect

sealed interface HomeSideEffect : BaseSideEffect {
    data class ShowMessage(
        val message: String,
    ) : HomeSideEffect
}
