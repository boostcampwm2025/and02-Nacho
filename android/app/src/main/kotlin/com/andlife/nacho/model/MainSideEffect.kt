package com.andlife.nacho.model

import android.content.Intent
import com.andlife.ui.base.BaseSideEffect

sealed interface MainSideEffect : BaseSideEffect {
    data class HandleDeepLink(
        val intent: Intent,
    ) : MainSideEffect

    data class NavigateToDetail(
        val invitationId: Long,
    ) : MainSideEffect
}
