package com.andlife.myinvitation.model

import com.andlife.ui.base.BaseUiState

data class MyInvitationUiState(
    val isRefreshing: Boolean = false,
    val selectedTab: Int = 0
) : BaseUiState
