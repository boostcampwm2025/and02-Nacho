package com.andlife.InvitationServer.service.invitation

import com.andlife.InvitationServer.entity.AnnouncementSection
import com.andlife.InvitationServer.entity.Invitation
import com.andlife.InvitationServer.entity.InvitationCard
import com.andlife.InvitationServer.entity.User
import com.andlife.InvitationServer.entity.InvitationParticipant
import com.andlife.InvitationServer.repository.invitation.AnnouncementRepository
import com.andlife.InvitationServer.repository.invitation.InvitationCardRepository
import com.andlife.InvitationServer.repository.invitation.InvitationRepository
import com.andlife.InvitationServer.repository.invitation.participant.InvitationParticipantRepository
import com.andlife.InvitationServer.request.invitation.CreateInvitationRequest
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
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
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
        sortType: String,
        pageable: Pageable
    ): PagingResponse<InvitationSummaryResponse> {
        val now = LocalDateTime.now()
        val upperStatus = status.uppercase()

        val direction = if (sortType.uppercase() == "DESC") Sort.Direction.DESC else Sort.Direction.ASC

        val sort = Sort.by(direction, "invitation.invitationDate")
            .and(Sort.by(direction, "invitation.startTime"))

        val adjustedPageable = PageRequest.of(pageable.pageNumber, pageable.pageSize, sort)

        val participantPage: Page<InvitationParticipant> = when (upperStatus) {
            "UPCOMING" -> participantRepository.findUpcomingInvitations(userId, now.toLocalDate(), now.toLocalTime(), adjustedPageable)
            "PAST" -> participantRepository.findPastInvitations(userId, now.toLocalDate(), now.toLocalTime(), adjustedPageable)
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

    @Transactional(readOnly = true)
    fun getMyInvitations(
        userId: Long,
        status: String,
        sortType: String,
        pageable: Pageable
    ): PagingResponse<InvitationSummaryResponse> {
        val now = LocalDateTime.now()
        val upperStatus = status.uppercase()

        val direction = if (sortType.uppercase() == "DESC") Sort.Direction.DESC else Sort.Direction.ASC

        val sort = Sort.by(direction, "invitationDate")
            .and(Sort.by(direction, "startTime"))

        val adjustedPageable = PageRequest.of(pageable.pageNumber, pageable.pageSize, sort)

        val invitationPage: Page<Invitation> = when (upperStatus) {
            "UPCOMING" -> invitationRepository.findUpcomingByHostId(userId, now.toLocalDate(), now.toLocalTime(), adjustedPageable)
            "PAST" -> invitationRepository.findPastByHostId(userId, now.toLocalDate(), now.toLocalTime(), adjustedPageable)
            else -> invitationRepository.findAllByHostId(userId, adjustedPageable)
        }

        val contents = invitationPage.content.map { invitation ->
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
                isEnd = invitationPage.isLast,
                pageableCount = invitationPage.numberOfElements,
                totalCount = invitationPage.totalElements,
                currentPage = invitationPage.number
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
                    backgroundColor = it.backgroundColor
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

    @Transactional
    fun createInvitation(request: CreateInvitationRequest): InvitationResponse {

        val invitationDate = LocalDate.parse(request.invitationDate)
        val startTime = LocalTime.parse(request.startTime, DateTimeFormatter.ofPattern("HH:mm"))
        val endTime = request.endTime?.let {
            LocalTime.parse(it, DateTimeFormatter.ofPattern("HH:mm"))
        }
        val invitation = Invitation(
            host = User(1, "donghyun@boostcamp.com", "동현", "https://picsum.photos/200/200?random=10"),
            title = request.title,
            displayHostName = request.displayHostName,
            thumbnailUrls = request.thumbnailUrls,
            invitationDate = invitationDate,
            startTime = startTime,
            endTime = endTime,
            placeName = request.placename,
            address = request.address,
            lat = request.latitude,
            lng = request.longitude,
            locationGuide = request.locationGuide,
        )

        val savedInvitation = invitationRepository.save(invitation)

        val savedCard = request.invitationCard?.let { cardRequest ->
            val card = InvitationCard(
                invitation = savedInvitation,
                contentJson = cardRequest.contentJson,
                backgroundColor = cardRequest.backgroundColor,
                backgroundImageUrl = cardRequest.backgroundImageUrl,
            )
            invitationCardRepository.save(card)
        }

        val savedAnnouncements = request.announcements.map { announcementRequest ->
            val announcement = AnnouncementSection(
                invitation = savedInvitation,
                title = announcementRequest.title,
                content = announcementRequest.content,
                displayOrder = announcementRequest.displayOrder,
            )
            announcementRepository.save(announcement)
        }

        return toInvitationResponse(
            invitation = savedInvitation,
            card = savedCard,
            announcements = savedAnnouncements,
        )
    }

    private fun toInvitationResponse(
        invitation: Invitation,
        card: InvitationCard?,
        announcements: List<AnnouncementSection>,
    ): InvitationResponse {
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
            invitationCard = card?.let {
                InvitationCardResponse(
                    id = it.id,
                    invitationId = invitation.id,
                    contentJson = it.contentJson,
                    backgroundColor = it.backgroundColor,
                    backgroundImageUrl = it.backgroundImageUrl,
                )
            },
            announcements = announcements.map { announcement ->
                AnnouncementResponse(
                    id = announcement.id,
                    invitationId = invitation.id,
                    title = announcement.title,
                    content = announcement.content,
                    displayOrder = announcement.displayOrder,
                )
            },
        )
    }
}
