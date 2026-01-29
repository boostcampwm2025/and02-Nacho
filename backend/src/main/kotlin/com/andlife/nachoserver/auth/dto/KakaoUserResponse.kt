package com.andlife.nachoserver.auth.dto

import com.fasterxml.jackson.annotation.JsonProperty


data class KakaoUserResponse(
    val id: Long,

    @JsonProperty("kakao_account")
    val kakaoAccount: KakaoAccount?
)

data class KakaoAccount(
    val email: String?,
    val profile: KakaoProfile?
)

data class KakaoProfile(
    val nickname: String?,

    @JsonProperty("profile_image_url")
    val profileImageUrl: String?
)
