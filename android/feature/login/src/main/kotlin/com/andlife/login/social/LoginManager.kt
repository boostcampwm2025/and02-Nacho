package com.andlife.login.social

import android.content.Context
import com.andlife.domain.error.LoginError
import com.andlife.domain.util.Result
import javax.inject.Inject
import javax.inject.Singleton

interface LoginManager {
    suspend fun login(
        socialType: SocialType,
        context: Context,
    ): Result<String, LoginError>

    suspend fun logout(socialType: SocialType): Boolean
}

@Singleton
class LoginManagerImpl @Inject constructor(
    private val kakaoHandler: KakaoHandler
) : LoginManager {

    override suspend fun login(
        socialType: SocialType,
        context: Context,
    ): Result<String, LoginError> {
        val socialHandler = getAuthHandler(socialType)
        return socialHandler.login(context)
    }

    override suspend fun logout(socialType: SocialType): Boolean {
        val socialHandler = getAuthHandler(socialType)
        return socialHandler.logout()
    }

    private fun getAuthHandler(socialType: SocialType): SocialHandler {
        return when (socialType) {
            SocialType.KAKAO -> kakaoHandler
        }
    }
}
