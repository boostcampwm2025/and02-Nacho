package com.andlife.InvitationServer.repository.invitation

import com.andlife.InvitationServer.entity.Invitation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface InvitationRepository : JpaRepository<Invitation, Long> {
    @Query(
        """
        SELECT i FROM Invitation i
        JOIN FETCH i.host
        WHERE i.id = :invitationId
    """
    )
    fun findByInvitationIdWithHost(@Param("invitationId") invitationId: Long): Invitation?

    @Query(
        """
            SELECT DISTINCT i FROM Invitation i
            JOIN FETCH i.host
            LEFT JOIN InvitationParticipant ip ON i.id = ip.invitation.id
            WHERE (i.host.id = :userId OR ip.user.id = :userId)
            AND i.invitationDate >= :today 
            AND i.invitationDate <= :limitDate
            ORDER BY i.invitationDate ASC, i.startTime ASC
        """
    )
    fun findUpcomingInvitationsWithinDays(
        @Param("userId") userId: Long,
        @Param("today") today: LocalDate,
        @Param("limitDate") limitDate: LocalDate,
        pageable: Pageable
    ): Page<Invitation>
}
