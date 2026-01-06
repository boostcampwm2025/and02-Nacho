package com.andlife.myinvitation.model

import com.andlife.ui.base.BaseUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class MyInvitationCollectionUiState(
    val isLoading: Boolean = false,
    val mediaItems: ImmutableList<MyInvitationCollectionUiModel> = persistentListOf(),
    val errorMessage: String? = null
): BaseUiState
