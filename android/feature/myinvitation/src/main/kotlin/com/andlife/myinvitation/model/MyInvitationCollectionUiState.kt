package com.andlife.myinvitation.model

import com.andlife.ui.base.BaseUiState

data class MyInvitationCollectionUiState(
    val isLoading: Boolean = false,
    val mediaItems: List<MyInvitationCollectionUiModel> = emptyList(),
    val errorMessage: String? = null
): BaseUiState
