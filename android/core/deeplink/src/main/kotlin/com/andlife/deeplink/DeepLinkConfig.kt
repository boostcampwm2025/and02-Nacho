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

    /**
     * 웹 딥링크 베이스 URL
     * @return https://invitationzzang.com/invite
     */
    fun buildWebDeepLinkBase(): String = "$SCHEME://$HOST/$PATH_INVITE/{id}"

    /**
     * 특정 초대장의 웹 딥링크
     * @param invitationId 초대장 ID
     * @return https://invitationzzang.com/invite/123
     */
    fun buildInvitationDeepLink(invitationId: Long): String {
        return "${buildWebDeepLinkBase()}/$invitationId"
    }

    /**
     * Uri 객체로 변환
     */
    fun buildInvitationDeepLinkUri(invitationId: Long): Uri {
        return buildInvitationDeepLink(invitationId).toUri()
    }

    /**
     * Play Store referrer URL (Deferred DeepLink용)
     * TODO: @param invitationId 초대장 ID
     * @return Play Store URL with referrer parameter
     */
    fun buildPlayStoreUrl(invitationId: Long): String {
        return "https://play.google.com/store/apps/details?" +
            "id=com.andlife.invitationzzang&" +
            "referrer=invite_id%3D$invitationId"
    }

    /**
     * Kakao 딥링크 패턴 (Navigation용)
     * @param kakaoKey Kakao Native App Key
     * @return kakao{key}://kakaolink?invite_id={id}
     */
    fun buildKakaoDeepLinkPattern(kakaoKey: String): String {
        return "$KAKAO_SCHEME_PREFIX$kakaoKey://$KAKAO_HOST?$PARAM_INVITE_ID={id}"
    }
}
