package com.andlife.myinvitation.model

import com.andlife.ui.base.BaseUiState

data class MyInvitationUiState(
    val isLoading: Boolean = false,
    val mediaItems: List<MyInvitationMediaUiModel> = emptyList(),
    val errorMessage: String? = null
): BaseUiState
