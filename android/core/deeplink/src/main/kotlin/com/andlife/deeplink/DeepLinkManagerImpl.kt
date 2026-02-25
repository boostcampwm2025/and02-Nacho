package com.andlife.deeplink

import androidx.core.net.toUri
import com.andlife.deeplink.di.KakaoNativeKey
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

class DeepLinkManagerImpl @Inject constructor(
    @param:KakaoNativeKey private val kakaoNativeKey: String,
) : DeepLinkManager {
    private val _deferredDeepLinkId = MutableSharedFlow<String?>(replay = 1)
    override val deferredDeepLinkId = _deferredDeepLinkId.asSharedFlow()

    override fun emitInvitationId(id: String?) {
        _deferredDeepLinkId.tryEmit(id)
    }

    override fun clearInvitationId() {
        _deferredDeepLinkId.tryEmit(null)
    }

    override fun getKakaoDeepLinkPattern(): String =
        "${DeepLinkConfig.KAKAO_SCHEME_PREFIX}$kakaoNativeKey://" +
            "${DeepLinkConfig.KAKAO_HOST}?" +
            "${DeepLinkConfig.KAKAO_PARAM_INVITE_ID}={id}"

    override fun buildKakaoDeepLinkUrl(invitationId: Long): String =
        "${DeepLinkConfig.KAKAO_SCHEME_PREFIX}$kakaoNativeKey://" +
            "${DeepLinkConfig.KAKAO_HOST}?" +
            "${DeepLinkConfig.KAKAO_PARAM_INVITE_ID}=$invitationId"

    override fun buildAppsFlyerUrl(invitationId: Long): String =
        DeepLinkConfig.AF_BASE_URL.toUri()
            .buildUpon()
            .appendQueryParameter("pid", DeepLinkConfig.AF_MEDIA_SOURCE)
            .appendQueryParameter("c", DeepLinkConfig.AF_CAMPAIGN)
            .appendQueryParameter(DeepLinkConfig.AF_DEEP_LINK_SUB1, invitationId.toString())
            .appendQueryParameter("af_dp", buildKakaoDeepLinkUrl(invitationId))
            .build()
            .toString()
}
