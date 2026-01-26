package com.andlife.domain.error

sealed interface LoginError : InvitationError {
    enum class SocialLoginError : LoginError {
        KAKAO
    }
}
