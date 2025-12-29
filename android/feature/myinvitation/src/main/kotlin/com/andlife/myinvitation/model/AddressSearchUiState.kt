package com.andlife.myinvitation.model

import androidx.compose.runtime.Stable
import com.andlife.ui.base.BaseUiState

@Stable
data class AddressSearchUiState(
    val query: String = ""
) : BaseUiState
