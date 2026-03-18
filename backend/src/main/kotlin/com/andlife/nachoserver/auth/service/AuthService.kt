package com.andlife.nachoserver.auth.service

import com.andlife.nachoserver.auth.client.KakaoAuthClient
import com.andlife.nachoserver.auth.dto.AuthResponse
import com.andlife.nachoserver.auth.dto.KakaoLoginRequest
import com.andlife.nachoserver.auth.dto.KakaoUserResponse
import com.andlife.nachoserver.auth.dto.RefreshTokenRequest
import com.andlife.nachoserver.auth.dto.UserResponse
import com.andlife.nachoserver.auth.jwt.JwtProvider
import com.andlife.nachoserver.entity.User
import com.andlife.nachoserver.repository.user.FcmTokenRepository
import com.andlife.nachoserver.repository.user.UserRepository
import com.andlife.nachoserver.util.FCMUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.random.Random

@Service
class AuthService(
    private val kakaoAuthClient: KakaoAuthClient,
    private val jwtProvider: JwtProvider,
    private val userRepository: UserRepository,
    private val fcmUtil: FCMUtil,
    private val fcmTokenRepository: FcmTokenRepository
) {

    @Transactional
    fun loginWithKakao(request: KakaoLoginRequest): AuthResponse {
        println("카카오 API 유효성그증")
        val kakaoUser = kakaoAuthClient.getUserInfo(request.accessToken)
        println("카카오 API 성공 ${kakaoUser}")
        val user = userRepository.findByKakaoId(kakaoUser.id)
            ?: createUser(kakaoUser)

        updateUserProfileIfNeeded(user, kakaoUser)

        return generateAuthResponse(user)
    }

    @Transactional
    fun loginWithTest(): AuthResponse {
        val testKakaoId = -System.currentTimeMillis()  // 거의 유일함

        val newUser = User(
            kakaoId = testKakaoId,
            email = "testUser${-testKakaoId}@test.com",
            profileImageUrl = "http://img1.kakaocdn.net/thumb/R640x640.q70/?fname=http://t1.kakaocdn.net/account_images/default_profile.jpeg",
            name = "임시"
        )
        val saved = userRepository.save(newUser)
        saved.name = "테스트유저${saved.id}"

        return generateAuthResponse(saved)
    }

    @Transactional(readOnly = true)
    fun refreshAccessToken(request: RefreshTokenRequest): AuthResponse {
        val refreshToken = request.refreshToken

        if (!jwtProvider.validateToken(refreshToken)) {
            throw IllegalArgumentException("Invalid refresh token")
        }

        if (!jwtProvider.isRefreshToken(refreshToken)) {
            throw IllegalArgumentException("Not a refresh token")
        }

        val userId = jwtProvider.getUserId(refreshToken)
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found") }

        return generateAuthResponse(user)
    }

    private fun createUser(
        kakaoUser: KakaoUserResponse
    ): User {
        val email = kakaoUser.kakaoAccount?.email
            ?: throw IllegalArgumentException("Email is required for registration")

        val name = kakaoUser.kakaoAccount.profile?.nickname
            ?: "User${kakaoUser.id}"

        val profileImageUrl = kakaoUser.kakaoAccount.profile?.profileImageUrl

        val newUser = User(
            kakaoId = kakaoUser.id,
            email = email,
            name = name,
            profileImageUrl = profileImageUrl
        )

        return userRepository.save(newUser)
    }

    private fun updateUserProfileIfNeeded(
        user: User,
        kakaoUser: KakaoUserResponse
    ) {
        val kakaoProfile = kakaoUser.kakaoAccount?.profile
        var updated = false

        kakaoProfile?.nickname?.let { newName ->
            if (user.name != newName) {
                user.name = newName
                updated = true
            }
        }

        kakaoProfile?.profileImageUrl?.let { newProfileUrl ->
            if (user.profileImageUrl != newProfileUrl) {
                user.profileImageUrl = newProfileUrl
                updated = true
            }
        }

        if (updated) {
            userRepository.save(user)
        }
    }

    private fun generateAuthResponse(user: User): AuthResponse {

        // 임시: 내 계정으로 로그인된 모든 기기에 로그인 알림 보내기
        val fcmTokens = fcmTokenRepository.findByUser(user)
        fcmUtil.sendToTokens(
            fcmTokens = fcmTokens.map { it.fcmToken },
            title = "로그인 알림",
            body = "나의 계정으로 로그인되었습니다.",
        )

        val accessToken = jwtProvider.generateAccessToken(user.id)
        val refreshToken = jwtProvider.generateRefreshToken(user.id)

        return AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            user = UserResponse(
                id = user.id,
                email = user.email,
                name = user.name,
                profileImageUrl = user.profileImageUrl
            )
        )
    }
}
