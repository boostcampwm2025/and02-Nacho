package com.andlife.home.model

import com.andlife.model.guestbook.AudioPlaybackState
import com.andlife.model.invitation.UpcomingInvitationUiModel
import com.andlife.ui.base.BaseUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class HomeUiState(
    val isRefreshing: Boolean = false,
    val isMediaPlaying: Boolean = false,
    val upcomingInvitations: ImmutableList<UpcomingInvitationUiModel> = persistentListOf(),
    val audioPlaybackState: AudioPlaybackState = AudioPlaybackState(),
) : BaseUiState {

    val canPlayVideo: Boolean
        get() = isMediaPlaying && !audioPlaybackState.isAudioLoading && !audioPlaybackState.isAudioPlaying
}
