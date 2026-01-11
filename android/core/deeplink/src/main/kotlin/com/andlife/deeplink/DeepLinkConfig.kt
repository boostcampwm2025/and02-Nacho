package com.andlife.deeplink

object DeepLinkConfig {
    const val KAKAO_SCHEME_PREFIX = "kakao"
    const val KAKAO_HOST = "kakaolink"
    const val KAKAO_PARAM_INVITE_ID = "invite_id"

    const val AF_BASE_URL = "https://invitationzzang.onelink.me/Z9oz/vjuvgm3g"
    const val AF_MEDIA_SOURCE = "kakao_share"
    const val AF_CAMPAIGN = "invitation_share"
    const val AF_PARAM_INVITE_ID = "invite_id"
    const val AF_IS_FIRST_LAUNCH = "is_first_launch"

    // TODO: Play Store 정식 출시 후에는 코드 제거 후 앱스플라이어 대시보드에 URL 등록
    const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.andlife.invitationzzang"
}
