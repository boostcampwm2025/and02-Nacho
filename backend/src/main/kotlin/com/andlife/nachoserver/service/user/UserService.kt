package com.andlife.nachoserver.service.user

import com.andlife.nachoserver.auth.AuthContext
import com.andlife.nachoserver.auth.dto.UserResponse
import com.andlife.nachoserver.auth.exception.TokenExpiredException
import com.andlife.nachoserver.repository.invitation.InvitationRepository
import com.andlife.nachoserver.repository.participant.InvitationParticipantRepository
import com.andlife.nachoserver.repository.user.UserRepository
import com.andlife.nachoserver.service.guestbook.GuestBookService
import com.andlife.nachoserver.service.invitation.InvitationService
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val invitationRepository: InvitationRepository,
    private val participantRepository: InvitationParticipantRepository,
    private val guestBookService: GuestBookService,
    private val invitationService: InvitationService
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

    @Transactional
    fun deleteUser(authContext: AuthContext): UserResponse {
        when (authContext) {
            is AuthContext.Member -> {
                val userId = authContext.userId
                val user = userRepository.findById(userId)
                    .orElseThrow { EntityNotFoundException("사용자를 찾을 수 없습니다.") }

                val hostedInvitations = invitationRepository.findAllByHostId(userId)
                hostedInvitations.forEach { invitation ->
                    invitationService.deleteInvitation(invitation.id, userId)
                }

                participantRepository.deleteAllByUserId(userId)
                guestBookService.deleteAllByUserId(userId)
                userRepository.deleteById(userId)

                return UserResponse(
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
