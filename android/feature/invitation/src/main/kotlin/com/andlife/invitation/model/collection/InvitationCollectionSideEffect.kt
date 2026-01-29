package com.andlife.invitation.model.collection

import com.andlife.ui.base.BaseSideEffect

sealed interface InvitationCollectionSideEffect : BaseSideEffect {
    data object DownloadSuccess : InvitationCollectionSideEffect
    data object DownloadFailed : InvitationCollectionSideEffect
}
