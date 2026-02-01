package com.andlife.nachoserver.service.thankscard

import com.andlife.nachoserver.entity.ThanksCard
import com.andlife.nachoserver.repository.invitation.InvitationRepository
import com.andlife.nachoserver.repository.thankscard.ThanksCardRepository
import com.andlife.nachoserver.request.thankscard.ThanksCardRequest
import com.andlife.nachoserver.response.thankscard.ThanksCardResponse
import com.andlife.nachoserver.response.thankscard.toResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ThanksCardService(
    private val thanksCardRepository: ThanksCardRepository,
    private val invitationRepository: InvitationRepository
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

        return thanksCardRepository.save(card).toResponse()
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

    fun getThanksCard(invitationId: Long): ThanksCardResponse {
        val card = thanksCardRepository.findByInvitationIdWithDetails(invitationId)
            ?: throw NoSuchElementException("Thanks card not found for invitation: $invitationId")

        return card.toResponse()
    }

    @Transactional
    fun deleteThanksCard(invitationId: Long) {
        thanksCardRepository.deleteByInvitationId(invitationId)
    }
}
