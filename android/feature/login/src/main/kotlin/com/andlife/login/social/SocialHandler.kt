package com.andlife.login.social

import android.content.Context
import com.andlife.domain.error.LoginError
import com.andlife.domain.util.Result

interface SocialHandler {
    suspend fun login(context: Context): Result<String, LoginError>
    suspend fun logout(): Boolean
}

enum class SocialType {
    KAKAO
}

