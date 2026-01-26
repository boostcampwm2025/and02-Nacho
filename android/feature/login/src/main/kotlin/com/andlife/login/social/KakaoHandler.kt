package com.andlife.login.social

import android.content.Context
import com.andlife.domain.error.LoginError
import com.andlife.domain.util.Result
import com.andlife.login.R
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class KakaoHandler @Inject constructor() : SocialHandler {

    override suspend fun login(context: Context): Result<String, LoginError> {
        try {
            val oauthToken: OAuthToken =
                if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                    loginWithKakaoTalk(context)
                } else {
                    loginWithKakaoAccount(context)
                }
            return Result.Success(oauthToken.accessToken)
        } catch (e: Exception) {
            return Result.Error(error = LoginError.SocialLoginError.KAKAO, message = e.message)
        }
    }

    override suspend fun logout(): Boolean {
        return suspendCancellableCoroutine { continuation ->
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    continuation.resume(false)
                } else {
                    continuation.resume(true)
                }
            }
        }
    }

    private suspend fun loginWithKakaoTalk(context: Context): OAuthToken =
        suspendCoroutine { cont ->
            UserApiClient.instance.loginWithKakaoTalk(context) { oauthToken, error ->
                if (error != null) {
                    cont.resumeWithException(error)
                } else if (oauthToken != null) {
                    cont.resume(oauthToken)
                } else {
                    cont.resumeWithException(Exception(context.getString(R.string.fail_kakao_login)))
                }
            }
        }

    private suspend fun loginWithKakaoAccount(context: Context): OAuthToken =
        suspendCoroutine { cont ->
            UserApiClient.instance.loginWithKakaoAccount(context) { oauthToken, error ->
                if (error != null) {
                    cont.resumeWithException(error)
                } else if (oauthToken != null) {
                    cont.resume(oauthToken)
                } else {
                    cont.resumeWithException(Exception(context.getString(R.string.fail_kakao_login)))
                }
            }
        }
}
