package com.andlife.nachoserver.request.user

data class UpdateProfileRequest(
    val nickname: String?,
    val profileImageUrl: String?
)