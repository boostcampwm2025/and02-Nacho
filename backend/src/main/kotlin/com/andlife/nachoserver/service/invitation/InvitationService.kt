package com.andlife.nachoserver.service.invitation

import com.andlife.nachoserver.auth.AuthContext
import com.andlife.nachoserver.entity.AnnouncementSection
import com.andlife.nachoserver.entity.Invitation
import com.andlife.nachoserver.entity.InvitationCard
import com.andlife.nachoserver.entity.InvitationParticipant
import com.andlife.nachoserver.repository.guestbook.GuestBookRepository
import com.andlife.nachoserver.repository.invitation.AnnouncementRepository
import com.andlife.nachoserver.repository.invitation.InvitationCardRepository
import com.andlife.nachoserver.repository.invitation.InvitationRepository
import com.andlife.nachoserver.repository.participant.InvitationParticipantRepository
import com.andlife.nachoserver.repository.user.UserRepository
import com.andlife.nachoserver.request.invitation.AnnouncementRequest
import com.andlife.nachoserver.request.invitation.CreateInvitationRequest
import com.andlife.nachoserver.request.invitation.InvitationCardRequest
import com.andlife.nachoserver.request.invitation.UpdateInvitationRequest
import com.andlife.nachoserver.response.PagingMetaResponse
import com.andlife.nachoserver.response.PagingResponse
import com.andlife.nachoserver.response.invitation.InvitationResponse
import com.andlife.nachoserver.response.invitation.InvitationSummaryResponse
import com.andlife.nachoserver.response.invitation.JoinResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import com.andlife.nachoserver.response.invitation.UpcomingInvitationResponse
import com.andlife.nachoserver.response.invitation.toInvitationResponse
import com.andlife.nachoserver.service.guestbook.GuestBookService
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
    private val participantRepository: InvitationParticipantRepository,
    private val userRepository: UserRepository,
    private val guestBookService: GuestBookService
) {
    @Transactional
    fun joinInvitation(
        invitationId: Long,
        userId: Long?,
        guestInvitationIds: List<Long>
    ): JoinResponse {
        return try {
            val invitation = invitationRepository.findById(invitationId)
                .orElseThrow { NoSuchElementException("초대장을 찾을 수 없습니다. ID: $invitationId") }

            println(">>> [초대장 조회 성공] ID: $invitationId")

            if (userId != null) {
                val isHost = invitation.host.id == userId
                if (isHost) {
                    return JoinResponse(
                        invitationId = invitationId,
                        isMember = true,
                        alreadyJoined = true
                    )
                }

                val isAlreadyJoined = participantRepository.existsByInvitationIdAndUserId(invitationId, userId)
                if (!isAlreadyJoined) {
                    val userProxy = userRepository.getReferenceById(userId)
                    participantRepository.save(InvitationParticipant(invitation = invitation, user = userProxy))
                }

                return JoinResponse(invitationId = invitationId, isMember = true, alreadyJoined = isAlreadyJoined)

            }

            val alreadyHasAccess = guestInvitationIds.contains(invitationId)
            val response = JoinResponse(invitationId = invitationId, isMember = false, alreadyJoined = alreadyHasAccess)
            response
        } catch (e: Exception) {
            println(">>> [서비스 에러] ${e.javaClass.simpleName}: ${e.message}")
            e.printStackTrace()
            throw e
        }
    }

    @Transactional
    fun leaveInvitation(invitationId: Long, userId: Long) {
        val participant = participantRepository.findByUserIdAndInvitationId(userId, invitationId)
            ?: throw NoSuchElementException("참여 정보를 찾을 수 없습니다. (UserID: $userId, InvitationID: $invitationId)")

        participantRepository.delete(participant)
    }

    fun leaveInvitationForGuest(invitationId: Long) {
        println("Guest left invitation: $invitationId")
    }

    @Transactional(readOnly = true)
    fun getParticipantInvitations(
        userId: Long?,
        guestInvitationIds: List<Long>,
        status: String,
        sortType: String,
        pageable: Pageable
    ): PagingResponse<InvitationSummaryResponse> {
        val upperStatus = status.uppercase()
        val direction = if (sortType.uppercase() == "DESC") Sort.Direction.DESC else Sort.Direction.ASC

        return if (userId != null) {
            val sort = Sort.by(direction, "invitation.invitationDate")
                .and(Sort.by(direction, "invitation.startTime"))
            val adjustedPageable = PageRequest.of(pageable.pageNumber, pageable.pageSize, sort)
            val now = LocalDateTime.now()

            val participantPage = when (upperStatus) {
                "UPCOMING" -> participantRepository.findUpcomingInvitations(userId, now.toLocalDate(), now.toLocalTime(), adjustedPageable)
                "PAST" -> participantRepository.findPastInvitations(userId, now.toLocalDate(), now.toLocalTime(), adjustedPageable)
                else -> participantRepository.findAllByUserIdWithInvitation(userId, adjustedPageable)
            }

            convertToPagingResponse(participantPage.map { it.invitation }, userId)
        } else {
            val sort = Sort.by(direction, "invitationDate")
                .and(Sort.by(direction, "startTime"))
            val adjustedPageable = PageRequest.of(pageable.pageNumber, pageable.pageSize, sort)

            if (guestInvitationIds.isEmpty()) {
                return PagingResponse.empty()
            }

            val invitationPage = invitationRepository.findAllByIdInWithStatus(
                guestInvitationIds, upperStatus, adjustedPageable
            )

            convertToPagingResponse(invitationPage, null)
        }
    }

    private fun convertToPagingResponse(
        page: Page<Invitation>,
        currentUserId: Long?
    ): PagingResponse<InvitationSummaryResponse> {
        val contents = page.content.map { invitation ->
            InvitationSummaryResponse(
                id = invitation.id,
                title = invitation.title,
                thumbnailUrls = invitation.thumbnailUrls,
                displayHostName = invitation.displayHostName,
                address = invitation.address,
                invitationDate = invitation.invitationDate.toString(),
                startTime = invitation.startTime.toString(),
                isOwner = invitation.host.id == currentUserId
            )
        }

        return PagingResponse(
            meta = PagingMetaResponse(
                isEnd = page.isLast,
                pageableCount = page.numberOfElements,
                totalCount = page.totalElements,
                currentPage = page.number
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

    @Transactional
    fun updateInvitationCard(cardId: Long, request: InvitationCardRequest): Long {
        val card = invitationCardRepository.findById(cardId)
            .orElseThrow { NoSuchElementException("Invitation card not found: $cardId") }

        card.contentJson = request.contentJson
        card.backgroundColor = request.backgroundColor
        card.backgroundImageUrl = request.backgroundImageUrl

        return invitationCardRepository.save(card).id
    }

    @Transactional
    fun deleteInvitation(invitationId: Long, userId: Long) {
        val invitation = invitationRepository.findByIdAndHostId(invitationId, userId)
            ?: throw NoSuchElementException("삭제 권한이 없거나 초대장을 찾을 수 없습니다. ID: $invitationId")

        // 연관된 데이터 삭제
        guestBookService.deleteAllByInvitation(invitationId)
        announcementRepository.deleteAllByInvitationId(invitationId)
        invitationCardRepository.deleteByInvitationId(invitationId)
        participantRepository.deleteAllByInvitationId(invitationId)

        invitationRepository.delete(invitation)
    }

    fun getInvitation(invitationId: Long): InvitationResponse {
        val invitation = invitationRepository.findByInvitationIdWithHost(invitationId)
            ?: throw NoSuchElementException("Invitation not found: $invitationId")

        val invitationCard = invitationCardRepository.findByInvitationIdWithDetails(invitationId)
        val announcements = announcementRepository.findAllByInvitationIdOrderByDisplayOrder(invitationId)

        return invitation.toInvitationResponse(
            card = invitationCard,
            announcements = announcements
        )
    }

    @Transactional
    fun createInvitation(userId: Long, request: CreateInvitationRequest): InvitationResponse {
        val host = userRepository.findById(userId)
            .orElseThrow { NoSuchElementException("User not found: $userId") }

        val invitationDate = LocalDate.parse(request.invitationDate)
        val startTime = LocalTime.parse(request.startTime, DateTimeFormatter.ofPattern("HH:mm"))
        val endTime = request.endTime?.let {
            LocalTime.parse(it, DateTimeFormatter.ofPattern("HH:mm"))
        }
        val invitation = Invitation(
            host = host,
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

        return savedInvitation.toInvitationResponse(
            card = savedCard,
            announcements = savedAnnouncements,
        )
    }

    @Transactional
    fun updateInvitation(invitationId: Long, request: UpdateInvitationRequest): InvitationResponse {
        val invitation = invitationRepository.findByInvitationIdWithHost(invitationId)
            ?: throw NoSuchElementException("Invitation not found: $invitationId")

        val invitationDate = LocalDate.parse(request.invitationDate)
        val startTime = LocalTime.parse(request.startTime, DateTimeFormatter.ofPattern("HH:mm"))
        val endTime = request.endTime?.let {
            LocalTime.parse(it, DateTimeFormatter.ofPattern("HH:mm"))
        }

        invitation.title = request.title
        invitation.displayHostName = request.displayHostName
        invitation.thumbnailUrls = request.thumbnailUrls
        invitation.invitationDate = invitationDate
        invitation.startTime = startTime
        invitation.endTime = endTime
        invitation.placeName = request.placename
        invitation.address = request.address
        invitation.lat = request.latitude
        invitation.lng = request.longitude
        invitation.locationGuide = request.locationGuide

        val savedInvitation = invitationRepository.save(invitation)
        val existingCard = invitationCardRepository.findByInvitationIdWithDetails(invitationId)
        val savedAnnouncements = replaceAnnouncements(savedInvitation, request.announcements)

        return invitation.toInvitationResponse(
            card = existingCard,
            announcements = savedAnnouncements
        )
    }

    private fun replaceAnnouncements(
        invitation: Invitation,
        announcementRequests: List<AnnouncementRequest>
    ): List<AnnouncementSection> {
        announcementRepository.deleteAllByInvitationId(invitation.id)

        return announcementRequests.map { req ->
            announcementRepository.save(
                AnnouncementSection(
                    invitation = invitation,
                    title = req.title,
                    content = req.content,
                    displayOrder = req.displayOrder
                )
            )
        }
    }

    fun getUpcomingInvitations(
        authContext: AuthContext,
        days: Long,
        pageable: Pageable
    ): PagingResponse<UpcomingInvitationResponse> {
        val today = LocalDate.now()
        val limitDate = today.plusDays(days)

        val upcomingInvitationsPage: Page<Invitation> = when (authContext) {
            is AuthContext.Member -> {
                invitationRepository.findUpcomingByParticipantIdWithinDays(
                    userId = authContext.userId,
                    startDate = today,
                    endDate = limitDate,
                    pageable = pageable
                )
            }
            is AuthContext.Guest -> {
                if (authContext.invitationIds.isEmpty()) {
                    Page.empty(pageable)
                } else {
                    invitationRepository.findAllByIdInAndDateRange(
                        ids = authContext.invitationIds,
                        startDate = today,
                        endDate = limitDate,
                        pageable = pageable
                    )
                }
            }
        }

        val currentUserId = (authContext as? AuthContext.Member)?.userId

        val contents = upcomingInvitationsPage.content.map { invitation ->
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
        }

        return PagingResponse(
            meta = PagingMetaResponse(
                isEnd = upcomingInvitationsPage.isLast,
                pageableCount = upcomingInvitationsPage.numberOfElements,
                totalCount = upcomingInvitationsPage.totalElements,
                currentPage = upcomingInvitationsPage.number
            ),
            content = contents
        )
    }
}
