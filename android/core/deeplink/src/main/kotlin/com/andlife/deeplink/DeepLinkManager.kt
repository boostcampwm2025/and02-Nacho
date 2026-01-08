package com.andlife.deeplink

import kotlinx.coroutines.flow.SharedFlow

interface DeepLinkManager {
    val deferredDeepLinkId: SharedFlow<String>
    fun emitInvitationId(id: String)

    fun getKakaoPattern(): String
    fun getWebBase(): String
}
