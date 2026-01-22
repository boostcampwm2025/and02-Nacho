package com.andlife.network.interceptor

import com.andlife.network.BuildConfig
import com.andlife.network.auth.AuthTokenProvider
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenProvider: AuthTokenProvider
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val builder = originalRequest.newBuilder()

//        val userId = tokenProvider.getUserId()
        val userId = 1L
        val invitationIds = tokenProvider.getInvitationIds()

        if (userId != null) {
            builder.addHeader(BuildConfig.HEADER_USER_ID, userId.toString())
        } else if (invitationIds.isNotEmpty()) {
            val idsString = invitationIds.joinToString(",")
            builder.addHeader(BuildConfig.HEADER_GUEST_IDS, idsString)
        }

        return chain.proceed(builder.build())
    }
}
