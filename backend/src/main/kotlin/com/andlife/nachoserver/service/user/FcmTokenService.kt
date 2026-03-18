package com.andlife.nachoserver.service.user

import com.andlife.nachoserver.auth.AuthContext
import com.andlife.nachoserver.entity.FcmToken
import com.andlife.nachoserver.entity.User
import com.andlife.nachoserver.repository.user.FcmTokenRepository
import com.andlife.nachoserver.repository.user.UserRepository
import com.andlife.nachoserver.response.user.FcmTokenResponse
import com.andlife.nachoserver.util.FCMUtil
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class FcmTokenService(
    private val fcmTokenRepository: FcmTokenRepository,
    private val userRepository: UserRepository,
    private val fcmUtil: FCMUtil
) {

    @Transactional
    fun putFcmToken(authContext: AuthContext, token: String): FcmTokenResponse {
        val userId = (authContext as? AuthContext.Member)?.userId
        val user = userId?.let { 
            userRepository.findById(it)
                .orElseThrow { EntityNotFoundException("사용자를 찾을 수 없습니다.") }
        }

        val fcmToken = fcmTokenRepository.findByFcmToken(token)

        if (fcmToken != null) {
            // 이미 존재하는 FCM 토큰이 있다면 updated_at만 업데이트
            fcmToken.updatedAt = LocalDateTime.now()
            return FcmTokenResponse(
                    fcmToken = fcmToken.fcmToken,
                    userId = fcmToken.user?.id,
                    updatedAt = fcmToken.updatedAt,
                    lastPushedAt = fcmToken.lastPushedAt
                )
        }

        // 새로운 FCM 토큰을 저장
        val newToken = FcmToken(
            fcmToken = token,
            user = user
        )
        return FcmTokenResponse(
            fcmToken = newToken.fcmToken,
            userId = newToken.user?.id,
            updatedAt = newToken.updatedAt,
            lastPushedAt = newToken.lastPushedAt
        )
    }

    // 특정 FCM 토큰으로 알림 전송
    fun sendNotificationToToken(fcmToken: String, title: String, body: String) {
        fcmUtil.sendToToken(fcmToken, title, body)
        
        // lastPushedAt 업데이트
        fcmTokenRepository.findByFcmToken(fcmToken)?.let { token ->
            token.lastPushedAt = LocalDateTime.now()
            fcmTokenRepository.save(token)
        }
    }

    // 여러 FCM 토큰으로 알림 전송
    @Transactional
    fun sendNotificationToTokens(fcmTokens: List<String>, title: String, body: String) {
        fcmTokens.forEach { token ->
            sendNotificationToToken(token, title, body)
        }
    }

    // 특정 유저의 모든 기기에 알림 전송
    @Transactional
    fun sendNotificationToUser(user: User, title: String, body: String) {
        val tokens = fcmTokenRepository.findByUser(user)
        tokens.forEach { token ->
            sendNotificationToToken(token.fcmToken, title, body)
        }
    }
}