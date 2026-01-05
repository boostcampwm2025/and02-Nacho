package com.andlife.myinvitation.util

import com.andlife.domain.model.MediaType as DomainMediaType
import com.andlife.ui.model.MediaType as UiMediaType

fun DomainMediaType.toUiType(): UiMediaType {
    return when (this) {
        DomainMediaType.IMAGE -> UiMediaType.IMAGE
        DomainMediaType.VIDEO -> UiMediaType.VIDEO
        DomainMediaType.AUDIO -> UiMediaType.AUDIO
    }
}
