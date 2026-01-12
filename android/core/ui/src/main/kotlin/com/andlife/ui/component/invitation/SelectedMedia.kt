package com.andlife.ui.component.invitation

import androidx.compose.runtime.Immutable
import com.andlife.ui.model.UiMediaType

@Immutable
data class SelectedMedia(
    val uri: String,
    val type: UiMediaType,
    val duration: Int? = null,
)
