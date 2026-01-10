package com.andlife.network.interceptor

import com.andlife.network.di.KakaoApiKey
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class KakaoAuthInterceptor @Inject constructor(
    @param:KakaoApiKey private val apiKey: String,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestWithAuth =
            originalRequest.newBuilder().header(AUTHORIZATION_HEADER, "$KAKAO_AUTH_PREFIX$apiKey").build()
        return chain.proceed(requestWithAuth)
    }

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val KAKAO_AUTH_PREFIX = "KakaoAK "
    }
}
