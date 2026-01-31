package com.andlife.network.interceptor

import com.andlife.network.BuildConfig
import com.andlife.network.auth.AuthTokenProvider
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenProvider: AuthTokenProvider
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val builder = originalRequest.newBuilder()

        val accessToken = runBlocking { tokenProvider.getAccessToken() }
        val invitationIds = tokenProvider.getInvitationIds()

        if (accessToken != null) {
            builder.addHeader(AUTH_HEADER, "$BEARER $accessToken")
        } else if (invitationIds.isNotEmpty()) {
            val idsString = invitationIds.joinToString(",")
            builder.addHeader(BuildConfig.HEADER_GUEST_IDS, idsString)
        }

        return chain.proceed(builder.build())
    }

    companion object {
        private const val AUTH_HEADER = "Authorization"
        private const val BEARER = "Bearer"
    }
}
