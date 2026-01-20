package com.andlife.InvitationServer.service.invitation

import com.andlife.InvitationServer.repository.invitation.AnnouncementRepository
import com.andlife.InvitationServer.repository.invitation.InvitationCardRepository
import com.andlife.InvitationServer.repository.invitation.InvitationRepository
import com.andlife.InvitationServer.response.invitation.AnnouncementResponse
import com.andlife.InvitationServer.response.invitation.InvitationCardResponse
import com.andlife.InvitationServer.response.invitation.InvitationResponse
import com.andlife.InvitationServer.response.invitation.UpcomingInvitationResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional(readOnly = true)
class InvitationService(
    private val invitationRepository: InvitationRepository,
    private val invitationCardRepository: InvitationCardRepository,
    private val announcementRepository: AnnouncementRepository,
) {

    fun getInvitation(invitationId: Long): InvitationResponse {
        val invitation = invitationRepository.findByInvitationIdWithHost(invitationId)
            ?: throw NoSuchElementException("Invitation not found: $invitationId")

        val invitationCard = invitationCardRepository.findByInvitationIdWithDetails(invitationId)
        val announcements = announcementRepository.findAllByInvitationIdOrderByDisplayOrder(invitationId)

        return InvitationResponse(
            id = invitation.id,
            hostId = invitation.host.id,
            title = invitation.title,
            displayHostName = invitation.displayHostName,
            hostProfileUrl = invitation.host.profileImageUrl,
            thumbnailUrls = invitation.thumbnailUrls,
            invitationDate = invitation.invitationDate.toString(),
            startTime = invitation.startTime.toString(),
            endTime = invitation.endTime?.toString(),
            placename = invitation.placeName,
            address = invitation.address,
            lat = invitation.lat,
            lng = invitation.lng,
            locationGuide = invitation.locationGuide,
            invitationCard = invitationCard?.let {
                InvitationCardResponse(
                    id = it.id,
                    invitationId = invitation.id,
                    contentJson = it.contentJson,
                    backgroundImageUrl = it.backgroundImageUrl,
                )
            },
            announcements = announcements.map {
                AnnouncementResponse(
                    id = it.id,
                    invitationId = invitation.id,
                    title = it.title,
                    content = it.content,
                    displayOrder = it.displayOrder,
                )
            },
        )
    }

    fun getUpcomingInvitations(userId: Long, days: Long): List<UpcomingInvitationResponse> {
        val today = LocalDate.now()
        val limitDate = today.plusDays(days)

        return invitationRepository.findUpcomingInvitationsWithinDays(
            userId = userId,
            today = today,
            limitDate = limitDate
        ).map { invitation ->
            UpcomingInvitationResponse(
                id = invitation.id,
                title = invitation.title,
                thumbnailUrl = invitation.thumbnailUrls.firstOrNull(),
                invitationDate = invitation.invitationDate.toString(),
                startTime = invitation.startTime.toString(),
                displayHostName = invitation.displayHostName,
                hostProfileUrl = invitation.host.profileImageUrl,
            )
        }
    }
}
