package com.andlife.nachoserver.auth.client

import com.andlife.nachoserver.auth.dto.KakaoUserResponse
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class KakaoAuthClient(
    private val webClientBuilder: WebClient.Builder
) {
    private val webClient: WebClient by lazy {
        webClientBuilder
            .baseUrl(KAKAO_API_BASE_URL)
            .build()
    }

    fun getUserInfo(accessToken: String): KakaoUserResponse {
        return webClient.get()
            .uri(USER_INFO_ENDPOINT)
            .header("Authorization", "Bearer $accessToken")
            .retrieve()
            .bodyToMono(KakaoUserResponse::class.java)
            .block() ?: throw IllegalStateException("Failed to get user info from Kakao")
    }

    companion object {
        private const val KAKAO_API_BASE_URL = "https://kapi.kakao.com"
        private const val USER_INFO_ENDPOINT = "/v2/user/me"
    }
}
