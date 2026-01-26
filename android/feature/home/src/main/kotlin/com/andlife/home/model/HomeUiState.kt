package com.andlife.home.model

import com.andlife.model.invitation.UpcomingInvitationUiModel
import com.andlife.ui.base.BaseUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class HomeUiState(
    val isRefreshing: Boolean = false,
    val upcomingInvitations: ImmutableList<UpcomingInvitationUiModel> = persistentListOf(),
    val playingAudioUrl: String? = null,
    val isAudioPlaying: Boolean = false,
    val audioCurrentPositionMs: Long = 0L,
    val audioDurationMs: Long = 0L,
) : BaseUiState
