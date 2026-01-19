package com.andlife.invitation.util

import com.andlife.domain.model.guestbook.MediaType
import com.andlife.model.guestbook.UiMediaType

/**
 * Domain의 MediaType을 UI의 UiMediaType으로 변환하는 확장 함수
 */
fun MediaType.toUiType(): UiMediaType =
    when (this) {
        MediaType.IMAGE -> UiMediaType.IMAGE
        MediaType.VIDEO -> UiMediaType.VIDEO
        MediaType.AUDIO -> UiMediaType.AUDIO
    }
