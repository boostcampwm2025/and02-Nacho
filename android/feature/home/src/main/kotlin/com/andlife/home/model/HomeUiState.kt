package com.andlife.home.model

import androidx.compose.ui.geometry.Rect
import com.andlife.media.audio.AudioPlaybackState
import com.andlife.model.guestbook.GuestBookUiModel
import com.andlife.model.invitation.UpcomingInvitationUiModel
import com.andlife.ui.base.BaseUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class HomeUiState(
    val isMediaPlaying: Boolean = false,
    val upcomingInvitations: ImmutableList<UpcomingInvitationUiModel> = persistentListOf(),
    val audioPlaybackState: AudioPlaybackState = AudioPlaybackState(),
    val showLoginDialog: Boolean = false,
    val reportTargetId: Long? = null,
    val fullscreenVideoUrl: String? = null,
    val fullscreenThumbnailUrl: String? = null,
    val fullscreenStartBounds: Rect? = null
) : BaseUiState {

    val canPlayVideo: Boolean
        get() = isMediaPlaying && !audioPlaybackState.isLoading && !audioPlaybackState.isPlaying

    fun isFullscreenVideoUrlValid(guestBook: GuestBookUiModel): Boolean {
        val url = fullscreenVideoUrl ?: return false
        return guestBook.visualMedias.any { it.url == url }
    }
}
