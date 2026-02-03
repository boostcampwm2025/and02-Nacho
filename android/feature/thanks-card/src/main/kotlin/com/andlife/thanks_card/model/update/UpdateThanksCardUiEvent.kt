package com.andlife.thanks_card.model.update

import com.andlife.ui.base.BaseUiEvent

sealed interface UpdateThanksCardUiEvent : BaseUiEvent {
    data object OnClickBack : UpdateThanksCardUiEvent

    data object OnClickSaveChanges : UpdateThanksCardUiEvent

    data object OnClickRetry : UpdateThanksCardUiEvent
}
