package com.andlife.nachoserver.service.user

import com.andlife.nachoserver.auth.AuthContext
import com.andlife.nachoserver.auth.exception.TokenExpiredException
import com.andlife.nachoserver.entity.FcmToken
import com.andlife.nachoserver.repository.user.FcmTokenRepository
import com.andlife.nachoserver.repository.user.UserRepository
import com.andlife.nachoserver.response.user.FcmTokenResponse
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FcmTokenService(
    private val fcmTokenRepository: FcmTokenRepository,
    private val userRepository: UserRepository
) {

    @Transactional
    fun putFcmToken(authContext: AuthContext, token: String): FcmTokenResponse {
        val userId = (authContext as? AuthContext.Member)?.userId
            ?: throw TokenExpiredException()
        val user = userRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("사용자를 찾을 수 없습니다.") }

        val fcmToken = fcmTokenRepository.findByFcmToken(token)

        if (fcmToken != null) {
            // 이미 존재하는 FCM 토큰이 있다면 updated_at만 업데이트
            // fcmToken.updatedAt = LocalDateTime.now() // TODO: FcmToken 엔티티에 updated_at 필드 추가
            return FcmTokenResponse(
                    fcmToken = fcmToken.fcmToken,
                    userId = fcmToken.user?.id,
                    createdAt = fcmToken.createdAt
                )
        }

        // 새로운 FCM 토큰을 저장
        val newToken = FcmToken(
            fcmToken = fcmToken,
            user = user
        )
        return FcmTokenResponse(
            fcmToken = newToken.fcmToken,
            userId = newToken.user?.id,
            createdAt = newToken.createdAt
        )
    }
}