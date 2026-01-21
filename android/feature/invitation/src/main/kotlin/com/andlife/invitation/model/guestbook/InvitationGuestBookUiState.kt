package com.andlife.invitation.model.guestbook

import com.andlife.domain.model.guestbook.GuestBook
import com.andlife.ui.base.BaseUiState
import com.andlife.ui.component.invitation.SelectedMedia
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class InvitationGuestBookUiState(
    val selectedMedias: ImmutableList<SelectedMedia> = persistentListOf(),
    val textContent: String = "",
    val isUploading: Boolean = false,
    val errorMessage: String? = null,
    val playingAudioUrl: String? = null,
    val isAudioPlaying: Boolean = false,
    val isLoadingGuestBooks: Boolean = false,
    val guestBooksErrorMessage: String? = null,
) : BaseUiState
