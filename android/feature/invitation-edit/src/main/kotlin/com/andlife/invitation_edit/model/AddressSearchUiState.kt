package com.andlife.invitation_edit.model

import androidx.compose.runtime.Stable
import com.andlife.ui.base.BaseUiState

@Stable
data class AddressSearchUiState(
    val query: String = "",
    val totalCount: Int = 0,
) : BaseUiState
