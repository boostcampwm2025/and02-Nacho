package com.andlife.deeplink

object DeepLinkConfig {
    const val KAKAO_SCHEME_PREFIX = "kakao"
    const val KAKAO_HOST = "kakaolink"
    const val PARAM_INVITE_ID = "invite_id"

    fun buildKakaoDeepLinkPattern(kakaoKey: String): String =
        "${KAKAO_SCHEME_PREFIX}$kakaoKey://${KAKAO_HOST}?${PARAM_INVITE_ID}={id}"

    // TODO: Deferred Deep Link를 위한 referrer 포함 URL
    fun buildPlayStoreUrl(invitationId: Long): String =
        "https://play.google.com/store/apps/details?" +
            "id=com.andlife.invitationzzang&" +
            "referrer=invite_id%3D$invitationId"
}
