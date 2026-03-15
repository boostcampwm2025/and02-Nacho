package com.andlife.nachoserver.service.user

import com.andlife.nachoserver.auth.AuthContext
import com.andlife.nachoserver.auth.exception.TokenExpiredException
import com.andlife.nachoserver.entity.FcmToken
import com.andlife.nachoserver.repository.user.FcmTokenRepository
import com.andlife.nachoserver.repository.user.UserRepository
import com.andlife.nachoserver.response.user.RegisterFcmTokenResponse
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FcmTokenService(
    private val fcmTokenRepository: FcmTokenRepository,
    private val userRepository: UserRepository
) {

    @Transactional
    fun registerFcmToken(authContext: AuthContext, fcmToken: String): RegisterFcmTokenResponse {
        val userId = (authContext as? AuthContext.Member)?.userId ?: throw TokenExpiredException()
        val user = userRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("사용자를 찾을 수 없습니다.") }

        fcmTokenRepository.findByUser(user)?.let {
            fcmTokenRepository.delete(it)
        }

        val newToken = FcmToken(
            fcmToken = fcmToken,
            user = user
        )
        
        val savedToken = fcmTokenRepository.save(newToken)

        return RegisterFcmTokenResponse(
            fcmToken = savedToken.fcmToken,
            userId = savedToken.user?.id,
            createdAt = savedToken.createdAt
        )
    }
}