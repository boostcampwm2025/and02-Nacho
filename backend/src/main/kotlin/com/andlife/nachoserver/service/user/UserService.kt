package com.andlife.nachoserver.service.user

import com.andlife.nachoserver.auth.AuthContext
import com.andlife.nachoserver.auth.dto.UserResponse
import com.andlife.nachoserver.auth.exception.TokenExpiredException
import com.andlife.nachoserver.error.BusinessException
import com.andlife.nachoserver.repository.user.UserRepository
import com.andlife.nachoserver.response.CommonResponseCode
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) {

    fun getMe(authContext: AuthContext): UserResponse {
        return when (authContext) {
            is AuthContext.Member -> {
                val user = userRepository.findById(authContext.userId)
                    .orElseThrow { EntityNotFoundException("사용자를 찾을 수 없습니다.") }

                UserResponse(
                    id = user.id,
                    email = user.email,
                    name = user.name,
                    profileImageUrl = user.profileImageUrl
                )
            }
            is AuthContext.Guest -> {
                throw TokenExpiredException()
            }
        }
    }
}
