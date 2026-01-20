package com.andlife.home.model

import com.andlife.model.invitation.UpcomingInvitationUiModel
import com.andlife.ui.base.BaseUiState

data class HomeUiState(
    val isUpcomingLoading: Boolean = true,
    val isUpcomingError: Boolean = false,
    val upcomingInvitations: List<UpcomingInvitationUiModel> = emptyList(),
    val playingAudioUrl: String? = null,
    val isAudioPlaying: Boolean = false,
) : BaseUiState
