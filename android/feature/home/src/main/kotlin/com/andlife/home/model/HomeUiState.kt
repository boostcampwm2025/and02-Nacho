package com.andlife.home.model

import com.andlife.model.invitation.UpcomingInvitationUiModel
import com.andlife.ui.base.BaseUiState

data class HomeUiState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val upcomingInvitations: List<UpcomingInvitationUiModel> = emptyList(),
    val playingAudioUrl: String? = null,
    val isAudioPlaying: Boolean = false,
) : BaseUiState
