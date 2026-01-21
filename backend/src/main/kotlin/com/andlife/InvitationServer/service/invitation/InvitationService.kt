package com.andlife.InvitationServer.service.invitation

import com.andlife.InvitationServer.entity.InvitationParticipant
import com.andlife.InvitationServer.repository.invitation.AnnouncementRepository
import com.andlife.InvitationServer.repository.invitation.InvitationCardRepository
import com.andlife.InvitationServer.repository.invitation.InvitationRepository
import com.andlife.InvitationServer.repository.invitation.participant.InvitationParticipantRepository
import com.andlife.InvitationServer.response.PagingMetaResponse
import com.andlife.InvitationServer.response.PagingResponse
import com.andlife.InvitationServer.response.invitation.AnnouncementResponse
import com.andlife.InvitationServer.response.invitation.InvitationCardResponse
import com.andlife.InvitationServer.response.invitation.InvitationResponse
import com.andlife.InvitationServer.response.invitation.InvitationSummaryResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class InvitationService(
    private val invitationRepository: InvitationRepository,
    private val invitationCardRepository: InvitationCardRepository,
    private val announcementRepository: AnnouncementRepository,
    private val participantRepository: InvitationParticipantRepository
) {
    @Transactional(readOnly = true)
    fun getParticipantInvitations(
        userId: Long,
        status: String,
        pageable: Pageable
    ): PagingResponse<InvitationSummaryResponse> {
        val now = LocalDateTime.now()
        val nowDate = now.toLocalDate()
        val nowTime = now.toLocalTime()
        val upperStatus = status.uppercase()

        val sort = if (upperStatus == "PAST") {
            Sort.by("invitation.invitationDate").descending()
                .and(Sort.by("invitation.startTime").descending())
        } else {
            Sort.by("invitation.invitationDate").ascending()
                .and(Sort.by("invitation.startTime").ascending())
        }

        val adjustedPageable = PageRequest.of(pageable.pageNumber, pageable.pageSize, sort)

        val participantPage: Page<InvitationParticipant> = when (upperStatus) {
            "UPCOMING" -> participantRepository.findUpcomingInvitations(userId, nowDate, nowTime, adjustedPageable)
            "PAST" -> participantRepository.findPastInvitations(userId, nowDate, nowTime, adjustedPageable)
            else -> participantRepository.findAllByUserIdWithInvitation(userId, adjustedPageable)
        }

        val contents = participantPage.content.map { participant ->
            val invitation = participant.invitation
            InvitationSummaryResponse(
                id = invitation.id,
                title = invitation.title,
                thumbnailUrls = invitation.thumbnailUrls,
                displayHostName = invitation.displayHostName,
                address = invitation.address,
                invitationDate = invitation.invitationDate.toString(),
                startTime = invitation.startTime.toString()
            )
        }

        return PagingResponse(
            meta = PagingMetaResponse(
                isEnd = participantPage.isLast,
                pageableCount = participantPage.numberOfElements,
                totalCount = participantPage.totalElements,
                currentPage = participantPage.number
            ),
            content = contents
        )
    }

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
}
