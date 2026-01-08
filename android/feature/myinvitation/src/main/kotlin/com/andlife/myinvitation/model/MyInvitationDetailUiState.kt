package com.andlife.myinvitation.model

import com.andlife.ui.base.BaseUiState
import com.andlife.deeplink.DeepLinkConfig

data class MyInvitationDetailUiState(
    val id: Long = 0L,
) : BaseUiState {
    val deepLinkUrl: String
        get() = DeepLinkConfig.buildInvitationDeepLink(id)
}
