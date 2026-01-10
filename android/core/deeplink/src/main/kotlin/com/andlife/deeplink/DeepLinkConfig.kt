package com.andlife.deeplink

import android.net.Uri
import androidx.core.net.toUri

object DeepLinkConfig {
    private const val SCHEME = "https"
    private const val HOST = "invitationzzang.com"
    private const val PATH_INVITE = "invite"

    const val KAKAO_SCHEME_PREFIX = "kakao"
    const val KAKAO_HOST = "kakaolink"
    const val PARAM_INVITE_ID = "invite_id"

    fun buildWebDeepLinkBase(): String = "$SCHEME://$HOST/$PATH_INVITE"

    fun buildKakaoDeepLinkPattern(kakaoKey: String): String {
        return "${KAKAO_SCHEME_PREFIX}${kakaoKey}://${KAKAO_HOST}?${PARAM_INVITE_ID}={id}"
    }

    fun buildInvitationDeepLink(invitationId: Long): String {
        return "${buildWebDeepLinkBase()}/$invitationId"
    }

    fun buildInvitationDeepLinkUri(invitationId: Long): Uri {
        return buildInvitationDeepLink(invitationId).toUri()
    }

    // TODO: Deferred Deep Link를 위한 referrer 포함 URL
    fun buildPlayStoreUrl(invitationId: Long): String {
        return "https://play.google.com/store/apps/details?" +
            "id=com.andlife.invitationzzang&" +
            "referrer=invite_id%3D$invitationId"
    }
}
