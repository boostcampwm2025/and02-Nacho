package com.andlife.invitation.model.guestbook

import com.andlife.domain.model.guestbook.GuestBook
import com.andlife.ui.base.BaseUiState
import com.andlife.ui.component.invitation.SelectedMedia
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlin.collections.sorted

data class InvitationGuestBookUiState(
    val selectedMedias: ImmutableList<SelectedMedia> = persistentListOf(),
    val textContent: String = "",
    val isUploading: Boolean = false,
    val originalTextContent: String = "",
    val originalMediaIds: Set<Long> = emptySet(),
    val errorMessage: String? = null,
    val playingAudioUrl: String? = null,
    val isAudioPlaying: Boolean = false,
    val editingGuestBookId: Long? = null,
    val isLoadingGuestBooks: Boolean = false,
    val guestBooksErrorMessage: String? = null,
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
}
