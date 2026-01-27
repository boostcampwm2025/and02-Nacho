package com.andlife.invitation.model.guestbook

import com.andlife.media.audio.AudioPlaybackState
import com.andlife.ui.base.BaseUiState
import com.andlife.ui.component.invitation.SelectedMedia
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class InvitationGuestBookUiState(
    val selectedMedias: ImmutableList<SelectedMedia> = persistentListOf(),
    val textContent: String = "",
    val isUploading: Boolean = false,
    val isMediaPlaying: Boolean = false,
    val originalTextContent: String = "",
    val originalMediaIds: Set<Long> = emptySet(),
    val errorMessage: String? = null,
    val audioPlaybackState: AudioPlaybackState = AudioPlaybackState(),
    val editingGuestBookId: Long? = null,
    val isLoadingGuestBooks: Boolean = false,
    val deleteTargetId: Long? = null,
    val guestBooksErrorMessage: String? = null,
    val isAudioRecording: Boolean = false,
    val audioRecordingDuration: Int = 0,
) : BaseUiState {

    val isContentChanged: Boolean
        get() {
            if (editingGuestBookId == null) return true
            val isTextChanged = textContent != originalTextContent
            val currentMediaIds = selectedMedias.mapNotNull { it.id }.toSet()
            val isMediaChanged = currentMediaIds != originalMediaIds

            val hasNewMedias = selectedMedias.any { it.id == null }

            return isTextChanged || isMediaChanged || hasNewMedias
        }

    val isSubmittable: Boolean
        get() = (textContent.isNotBlank() || selectedMedias.isNotEmpty()) && !isUploading && isContentChanged

    val canPlayVideo: Boolean
        get() = isMediaPlaying && !audioPlaybackState.isLoading && !audioPlaybackState.isPlaying
}
