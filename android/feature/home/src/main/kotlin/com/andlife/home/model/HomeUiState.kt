package com.andlife.home.model

import com.andlife.model.guestbook.GuestBookUiModel
import com.andlife.ui.base.BaseUiState

data class HomeUiState(
    val isLoading: Boolean = false,
    val guestBooks: List<GuestBookUiModel> = emptyList(),
) : BaseUiState
