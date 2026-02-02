package com.andlife.thanks_card.model.create

import com.andlife.ui.base.BaseSideEffect

sealed interface CreateThanksSideEffect : BaseSideEffect {
    data object FailCreateThanksCard : CreateThanksSideEffect
    data object SuccessCreateThanksCard : CreateThanksSideEffect
    data object OnBack : CreateThanksSideEffect
}
