package com.andlife.invitation.util

import com.andlife.domain.model.MediaType as DomainMediaType
import com.andlife.ui.model.MediaType as UiMediaType

/**
 * Domain의 MediaType을 UI의 MediaType으로 변환하는 확장 함수
 */
fun DomainMediaType.toUiType(): UiMediaType {
    return when (this) {
        DomainMediaType.IMAGE -> UiMediaType.IMAGE
        DomainMediaType.VIDEO -> UiMediaType.VIDEO
        DomainMediaType.AUDIO -> UiMediaType.AUDIO
    }
}
