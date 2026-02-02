package com.andlife.thanks_card.model.create

import com.andlife.ui.base.BaseUiEvent

sealed interface CreateThanksUiEvent : BaseUiEvent {
    data object OnClickCreate : CreateThanksUiEvent
    data object OnClickBack : CreateThanksUiEvent
}
