package com.andlife.myinvitation.model

import com.andlife.ui.base.BaseUiState

data class MyInvitationUiState(
    val selectedTab: Int = 0,
    val upcomingTotalCount: Int = 0,
    val pastTotalCount: Int = 0
) : BaseUiState
