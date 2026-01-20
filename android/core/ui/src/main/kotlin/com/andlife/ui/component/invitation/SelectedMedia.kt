package com.andlife.ui.component.invitation

import androidx.compose.runtime.Immutable
import com.andlife.model.guestbook.UiMediaType

@Immutable
data class SelectedMedia(
    val id: Long? = null,
    val uri: String,
    val type: UiMediaType,
    val duration: Int? = null,
    val thumbnailUrl: String? = null,
)
