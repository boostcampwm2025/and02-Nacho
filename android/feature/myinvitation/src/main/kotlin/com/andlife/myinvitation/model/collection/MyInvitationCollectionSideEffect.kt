package com.andlife.myinvitation.model.collection

import com.andlife.ui.base.BaseSideEffect

sealed interface MyInvitationCollectionSideEffect : BaseSideEffect {
    data object DownloadFailed : MyInvitationCollectionSideEffect
    data object ShowDownloadGuide : MyInvitationCollectionSideEffect
}
