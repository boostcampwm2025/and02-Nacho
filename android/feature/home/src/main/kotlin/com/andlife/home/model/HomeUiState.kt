package com.andlife.home.model

import com.andlife.model.guestbook.GuestBookUiModel
import com.andlife.model.invitation.UpcomingScheduleUiModel
import com.andlife.ui.base.BaseUiState

data class HomeUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val upcomingSchedules: List<UpcomingScheduleUiModel> = emptyList(),
    val guestBooks: List<GuestBookUiModel> = emptyList(),
) : BaseUiState
