package com.andlife.invitation_card.model.updatecard

import com.andlife.ui.base.BaseSideEffect

sealed interface UpdateCardSideEffect : BaseSideEffect {
    data object FailUpdateCard: UpdateCardSideEffect
    data object SuccessUpdateCard: UpdateCardSideEffect
    data object OnBackNavigation: UpdateCardSideEffect
}
