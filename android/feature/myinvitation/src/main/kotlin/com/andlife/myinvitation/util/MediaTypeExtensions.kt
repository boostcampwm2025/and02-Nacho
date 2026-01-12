package com.andlife.myinvitation.util

import com.andlife.domain.model.guestbook.MediaType
import com.andlife.ui.model.UiMediaType

fun MediaType.toUiType(): UiMediaType =
    when (this) {
        MediaType.IMAGE -> UiMediaType.IMAGE
        MediaType.VIDEO -> UiMediaType.VIDEO
        MediaType.AUDIO -> UiMediaType.AUDIO
    }
