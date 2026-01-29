package com.andlife.invitation.model.collection

import com.andlife.domain.model.guestbook.DownloadState
import com.andlife.model.collection.CollectionUiModel
import com.andlife.ui.base.BaseUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class InvitationCollectionUiState(
    val isLoading: Boolean = false,
    val mediaItems: ImmutableList<CollectionUiModel> = persistentListOf(),
    val errorMessage: String? = null,
    val isDetailMode: Boolean = false,
    val selectedIndex: Int = 0,
    val isTextExpanded: Boolean = false,
    val downloadState: DownloadState = DownloadState.Idle,
    val networkDialogDismissed: Boolean = false
) : BaseUiState
