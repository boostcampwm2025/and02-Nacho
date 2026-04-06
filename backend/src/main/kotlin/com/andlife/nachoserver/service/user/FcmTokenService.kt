package com.andlife.nachoserver.service.user

import com.andlife.nachoserver.auth.AuthContext
import com.andlife.nachoserver.entity.FcmToken
import com.andlife.nachoserver.entity.User
import com.andlife.nachoserver.repository.user.FcmTokenRepository
import com.andlife.nachoserver.repository.user.UserRepository
import com.andlife.nachoserver.response.user.FcmTokenResponse
import com.andlife.nachoserver.util.FCMUtil
import com.andlife.nachoserver.util.FcmSendResult
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
            // 이미 존재하는 FCM 토큰이 있다면 유저 업데이트시키기
            fcmToken.user = user
            val updatedToken = fcmTokenRepository.save(fcmToken)

            return FcmTokenResponse(
                    fcmToken = updatedToken.fcmToken,
                    userId = updatedToken.user?.id,
                    lastPushedAt = updatedToken.lastPushedAt,
                    createdAt = updatedToken.createdAt,
                    updatedAt = updatedToken.updatedAt
                )
        }

        // 새로운 FCM 토큰을 저장
        val newToken = FcmToken(
            fcmToken = token,
            user = user
        )
        val savedToken = fcmTokenRepository.save(newToken)
        return FcmTokenResponse(
            fcmToken = savedToken.fcmToken,
            userId = savedToken.user?.id,
            lastPushedAt = savedToken.lastPushedAt,
            createdAt = savedToken.createdAt,
            updatedAt = savedToken.updatedAt
        )
    }

    // 특정 FCM 토큰으로 알림 전송
    fun sendNotificationToToken(fcmToken: String, title: String, body: String, data: Map<String, String>? = null) {
        val result = fcmUtil.sendToToken(fcmToken, title, body, data)
        
        when (result) {
            FcmSendResult.SUCCESS -> {
                // 성공 시 lastPushedAt 업데이트
                fcmTokenRepository.findByFcmToken(fcmToken)?.let { token ->
                    token.lastPushedAt = LocalDateTime.now()
                    fcmTokenRepository.save(token)
                }
            }
            FcmSendResult.INVALID_TOKEN, FcmSendResult.UNREGISTERED -> {
                // 만료된 또는 잘못된 토큰이면 DB에서 삭제
                fcmTokenRepository.findByFcmToken(fcmToken)?.let { token ->
                    fcmTokenRepository.delete(token)
                    println("FCM 토큰 삭제: $fcmToken")
                }
            }
            FcmSendResult.OTHER_ERROR -> {
                // 기타 에러는 로그만 남기고 토큰 유지
                println("FCM 메시지 전송 에러: $fcmToken")
            }
        }
    }

    // 여러 FCM 토큰으로 알림 전송
    @Transactional
    fun sendNotificationToTokens(fcmTokens: List<String>, title: String, body: String, data: Map<String, String>? = null) {
        fcmTokens.forEach { token ->
            sendNotificationToToken(token, title, body, data)
        }
    }

    // 특정 유저의 모든 기기에 알림 전송
    @Transactional
    fun sendNotificationToUser(user: User, title: String, body: String, data: Map<String, String>? = null) {
        val tokens = fcmTokenRepository.findByUser(user)
        tokens.forEach { token ->
            sendNotificationToToken(token.fcmToken, title, body, data)
        }
    }
}