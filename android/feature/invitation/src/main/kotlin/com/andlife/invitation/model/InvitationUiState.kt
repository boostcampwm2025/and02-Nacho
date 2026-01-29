package com.andlife.invitation.model

import com.andlife.ui.base.BaseUiState

data class InvitationUiState(
    val isRefreshing: Boolean = false,
    val selectedTab: Int = 0,
    val upcomingTotalCount: Int = 0,
    val pastTotalCount: Int = 0
) : BaseUiState
