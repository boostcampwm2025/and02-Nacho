package com.andlife.nachoserver.service.thankscard

import com.andlife.nachoserver.entity.ThanksCard
import com.andlife.nachoserver.repository.invitation.InvitationRepository
import com.andlife.nachoserver.repository.thankscard.ThanksCardRepository
import com.andlife.nachoserver.request.thankscard.ThanksCardRequest
import com.andlife.nachoserver.response.thankscard.ThanksCardResponse
import com.andlife.nachoserver.response.thankscard.toResponse
import com.andlife.nachoserver.service.user.FcmTokenService
import com.andlife.nachoserver.repository.participant.InvitationParticipantRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ThanksCardService(
    private val thanksCardRepository: ThanksCardRepository,
    private val invitationRepository: InvitationRepository,
    private val fcmTokenService: FcmTokenService,
    private val invitationParticipantRepository: InvitationParticipantRepository
) {

    @Transactional
    fun createThanksCard(invitationId: Long, request: ThanksCardRequest): ThanksCardResponse {
        val invitation = invitationRepository.findById(invitationId)
            .orElseThrow { NoSuchElementException("Invitation not found: $invitationId") }

        val existingCard = thanksCardRepository.findByInvitationIdWithDetails(invitationId)
        if (existingCard != null) {
            throw IllegalStateException("Thanks card already exists for invitation: $invitationId")
        }

        val card = ThanksCard(
            invitation = invitation,
            contentJson = request.contentJson,
            backgroundColor = request.backgroundColor,
            backgroundImageUrl = request.backgroundImageUrl
        )

        val savedCard = thanksCardRepository.save(card)
        sendThanksCardNotificationToParticipants(savedCard)
        return savedCard.toResponse()
    }

    @Transactional
    fun updateThanksCard(cardId: Long, request: ThanksCardRequest): ThanksCardResponse {
        val card = thanksCardRepository.findById(cardId)
            .orElseThrow { NoSuchElementException("Thanks card not found: $cardId") }

        card.contentJson = request.contentJson
        card.backgroundColor = request.backgroundColor
        card.backgroundImageUrl = request.backgroundImageUrl

        return thanksCardRepository.save(card).toResponse()
    }

    fun getThanksCard(cardId: Long): ThanksCardResponse {
        val card = thanksCardRepository.findById(cardId)
            .orElseThrow { NoSuchElementException("Thanks card not found: $cardId") }

        return card.toResponse()
    }

    @Transactional
    fun deleteThanksCard(invitationId: Long) {
        thanksCardRepository.deleteByInvitationId(invitationId)
    }

    private fun sendThanksCardNotificationToParticipants(thanksCard: ThanksCard) {
        try {
            val participants = invitationParticipantRepository.findAllByInvitationIdWithUser(thanksCard.invitation.id)
            val data = mapOf("invitation_id" to thanksCard.invitation.id.toString())
            
            participants.forEach { participant ->
                fcmTokenService.sendNotificationToUser(
                    participant.user,
                    "감사카드 도착",
                    "'${thanksCard.invitation.title}'의 감사카드가 도착했습니다.",
                    data
                )
            }
        } catch (e: Exception) {
            println("감사카드 알림 전송 실패: ${e.message}")
        }
    }
}
