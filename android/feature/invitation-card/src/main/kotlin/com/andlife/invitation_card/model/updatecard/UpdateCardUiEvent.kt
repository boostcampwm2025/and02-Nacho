package com.andlife.invitation_card.model.updatecard

import com.andlife.ui.base.BaseUiEvent

sealed interface UpdateCardUiEvent : BaseUiEvent {
    data object OnClickUpdateCard : UpdateCardUiEvent
    data object OnClickBackNavigation : UpdateCardUiEvent
}
