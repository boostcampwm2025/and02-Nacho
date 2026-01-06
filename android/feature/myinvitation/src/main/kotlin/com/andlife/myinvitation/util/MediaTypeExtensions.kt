package com.andlife.myinvitation.util

import com.andlife.domain.model.MediaType
import com.andlife.ui.model.UiMediaType

fun MediaType.toUiType(): UiMediaType {
    return when (this) {
        MediaType.IMAGE -> UiMediaType.IMAGE
        MediaType.VIDEO -> UiMediaType.VIDEO
        MediaType.AUDIO -> UiMediaType.AUDIO
    }
}
