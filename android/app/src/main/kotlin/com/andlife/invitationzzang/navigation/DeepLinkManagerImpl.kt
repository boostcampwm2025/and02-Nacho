package com.andlife.invitationzzang.navigation

import com.andlife.deeplink.DeepLinkConfig
import com.andlife.deeplink.DeepLinkManager
import com.andlife.network.di.KakaoNativeKey
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

class DeepLinkManagerImpl @Inject constructor(
    @param:KakaoNativeKey private val kakaoKey: String
) : DeepLinkManager {
    private val _id = MutableSharedFlow<String>(replay = 1)
    override val deferredDeepLinkId = _id.asSharedFlow()

    override fun emitInvitationId(id: String) { _id.tryEmit(id) }

    override fun getKakaoPattern() = DeepLinkConfig.buildKakaoDeepLinkPattern(kakaoKey)
    override fun getWebBase() = DeepLinkConfig.buildWebDeepLinkBase()
}
