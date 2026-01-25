package com.andlife.InvitationServer.service.invitation

import com.andlife.InvitationServer.auth.AuthContext
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
import com.andlife.InvitationServer.request.invitation.InvitationCardRequest
import com.andlife.InvitationServer.response.CommonResponseCode
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
import com.andlife.InvitationServer.response.invitation.UpcomingInvitationResponse
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
                startTime = invitation.startTime.toString(),
                isOwner = invitation.host.id == userId
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
                startTime = invitation.startTime.toString(),
                isOwner = invitation.host.id == userId
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

    @Transactional
    fun createInvitationCard(invitationId: Long, request: InvitationCardRequest): Long {
        val invitation = invitationRepository.findById(invitationId)
            .orElseThrow { NoSuchElementException("Invitation not found: $invitationId") }

        val existingCard = invitationCardRepository.findByInvitationIdWithDetails(invitationId)
        if (existingCard != null) {
            throw IllegalStateException("Invitation card already exists for invitation: $invitationId")
        }

        val card = InvitationCard(
            invitation = invitation,
            contentJson = request.contentJson,
            backgroundColor = request.backgroundColor,
            backgroundImageUrl = request.backgroundImageUrl,
        )

        return invitationCardRepository.save(card).id
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

    fun getUpcomingInvitations(
        authContext: AuthContext,
        days: Long,
        pageable: Pageable
    ): PagingResponse<UpcomingInvitationResponse> {

        val today = LocalDate.now()
        val limitDate = today.plusDays(days)

        val currentUserId = (authContext as? AuthContext.Member)?.userId

        val upcomingInvitationsPage = when (authContext) {
            is AuthContext.Member -> {
                invitationRepository.findUpcomingInvitationsWithinDays(
                    userId = authContext.userId,
                    today = today,
                    limitDate = limitDate,
                    pageable = pageable
                )
            }
            is AuthContext.Guest -> {
                Page.empty(pageable) //TODO: 비로그인 유저 임시 빈값 조회
            }
        }

        val responsePage = upcomingInvitationsPage.map { invitation ->
            try {
                UpcomingInvitationResponse(
                    id = invitation.id,
                    hostId = invitation.host.id,
                    isOwner = invitation.host.id == currentUserId,
                    title = invitation.title,
                    thumbnailUrl = invitation.thumbnailUrls.firstOrNull(),
                    invitationDate = invitation.invitationDate.toString(),
                    startTime = invitation.startTime.toString(),
                    displayHostName = invitation.displayHostName,
                    hostProfileUrl = invitation.host.profileImageUrl,
                )
            } catch (e: Exception) {
                println("ERROR: Mapping failed for Invitation ID ${invitation.id}: ${e.message}")
                throw e
            }
        }

        return PagingResponse(
            meta = PagingMetaResponse(
                isEnd = !responsePage.hasNext(),
                pageableCount = responsePage.numberOfElements,
                totalCount = responsePage.totalElements,
                currentPage = responsePage.number + 1
            ),
            content = responsePage.content
        )
    }
}
