package com.andlife.deeplink

import android.net.Uri
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
            "${DeepLinkConfig.KAKAO_PARAM_INVITE_ID}={invite_id}"

    override fun buildKakaoDeepLinkUrl(invitationId: Long): String =
        "${DeepLinkConfig.KAKAO_SCHEME_PREFIX}$kakaoNativeKey://" +
            "${DeepLinkConfig.KAKAO_HOST}?" +
            "${DeepLinkConfig.KAKAO_PARAM_INVITE_ID}=$invitationId"

    override fun buildAppsFlyerUrl(invitationId: Long): String =
        Uri
            .parse(DeepLinkConfig.AF_BASE_URL)
            .buildUpon()
            .appendQueryParameter("pid", DeepLinkConfig.AF_MEDIA_SOURCE)
            .appendQueryParameter("c", DeepLinkConfig.AF_CAMPAIGN)
            .appendQueryParameter(DeepLinkConfig.AF_PARAM_INVITE_ID, invitationId.toString())
            .appendQueryParameter("af_dp", buildKakaoDeepLinkUrl(invitationId))
            .appendQueryParameter("af_android_url", DeepLinkConfig.PLAY_STORE_URL) // TODO: Play Store 정식 출시 후 제거
            .appendQueryParameter("af_web_dp", DeepLinkConfig.PLAY_STORE_URL) // TODO: Play Store 정식 출시 후 제거
            .build()
            .toString()
}
