package com.andlife.invitation.model.guestbook

import com.andlife.domain.model.guestbook.GuestBook
import com.andlife.ui.base.BaseUiState
import com.andlife.ui.component.invitation.SelectedMedia

data class InvitationGuestBookUiState(
    val selectedMedias: List<SelectedMedia> = emptyList(),
    val textContent: String = "",
    val isUploading: Boolean = false,
    val errorMessage: String? = null,
    val guestBooks: List<GuestBook> = emptyList(),
    val isLoadingGuestBooks: Boolean = false,
    val guestBooksErrorMessage: String? = null,
) : BaseUiState
