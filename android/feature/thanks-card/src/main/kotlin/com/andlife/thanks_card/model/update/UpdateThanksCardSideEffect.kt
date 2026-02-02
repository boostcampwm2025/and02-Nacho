package com.andlife.thanks_card.model.update

import com.andlife.ui.base.BaseSideEffect

sealed interface UpdateThanksCardSideEffect : BaseSideEffect {
    data object OnSuccessUpdateThanksCard : UpdateThanksCardSideEffect
    data object OnFailUpdateThanksCard : UpdateThanksCardSideEffect
    data object OnBack : UpdateThanksCardSideEffect
}
