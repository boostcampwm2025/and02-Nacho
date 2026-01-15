package com.andlife.deeplink

import kotlinx.coroutines.flow.SharedFlow

interface DeepLinkManager {
    val deferredDeepLinkId: SharedFlow<String?>

    fun emitInvitationId(id: String?)

    fun clearInvitationId()

    fun getKakaoDeepLinkPattern(): String

    fun buildKakaoDeepLinkUrl(invitationId: Long): String

    fun buildAppsFlyerUrl(invitationId: Long): String
}
