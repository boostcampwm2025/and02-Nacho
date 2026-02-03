package com.andlife.thanks_card.model.update

import com.andlife.ui.base.BaseUiState

data class UpdateThanksCardUiState(
    val isLoading: Boolean = true,
    val isError: Boolean = false,
) : BaseUiState
