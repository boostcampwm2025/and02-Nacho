package com.andlife.nachoserver.auth.controller

import com.andlife.nachoserver.auth.dto.AuthResponse
import com.andlife.nachoserver.auth.dto.KakaoLoginRequest
import com.andlife.nachoserver.auth.dto.RefreshTokenRequest
import com.andlife.nachoserver.auth.service.AuthService
import com.andlife.nachoserver.response.BaseResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/login")
    fun loginWithKakao(
        @RequestBody request: KakaoLoginRequest
    ): BaseResponse<AuthResponse> {
        val response = authService.loginWithKakao(request)
        return BaseResponse.success(response)
    }

    @PostMapping("/reissue")
    fun refreshToken(
        @RequestBody request: RefreshTokenRequest
    ): BaseResponse<AuthResponse> {
        val response = authService.refreshAccessToken(request)
        return BaseResponse.success(response)
    }
}
