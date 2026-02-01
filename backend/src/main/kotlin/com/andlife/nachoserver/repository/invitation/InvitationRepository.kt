package com.andlife.nachoserver.repository.invitation

import com.andlife.nachoserver.entity.Invitation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate
import java.time.LocalTime

interface InvitationRepository : JpaRepository<Invitation, Long> {
    @Query(
        """
        SELECT i FROM Invitation i
        JOIN FETCH i.host
        WHERE i.id = :invitationId
    """
    )
    fun findByInvitationIdWithHost(@Param("invitationId") invitationId: Long): Invitation?

    fun findAllByHostId(hostId: Long, pageable: Pageable): Page<Invitation>

    // 다가오는 초대
    @Query("""
        SELECT i FROM Invitation i 
        WHERE i.host.id = :hostId 
        AND (i.invitationDate > :nowDate 
             OR (i.invitationDate = :nowDate AND i.startTime >= :nowTime))
    """)
    fun findUpcomingByHostId(hostId: Long, nowDate: LocalDate, nowTime: LocalTime, pageable: Pageable): Page<Invitation>

    // 지난 초대
    @Query("""
        SELECT i FROM Invitation i 
        WHERE i.host.id = :hostId 
        AND (i.invitationDate < :nowDate 
             OR (i.invitationDate = :nowDate AND i.startTime < :nowTime))
    """)
    fun findPastByHostId(hostId: Long, nowDate: LocalDate, nowTime: LocalTime, pageable: Pageable): Page<Invitation>

    @Query("""
    SELECT DISTINCT i FROM Invitation i
    JOIN FETCH i.host
    LEFT JOIN InvitationParticipant ip ON i.id = ip.invitation.id
    WHERE (i.host.id = :userId OR ip.user.id = :userId)
    AND (
        i.invitationDate > :startDate
        OR (i.invitationDate = :startDate AND i.startTime >= :nowTime)
    )
    AND i.invitationDate <= :endDate
    ORDER BY i.invitationDate ASC, i.startTime ASC
""")
    fun findUpcomingByParticipantIdWithinDays(
        @Param("userId") userId: Long,
        @Param("startDate") startDate: LocalDate,
        @Param("nowTime") nowTime: LocalTime,
        @Param("endDate") endDate: LocalDate,
        pageable: Pageable
    ): Page<Invitation>

    @Query("""
    SELECT i FROM Invitation i
    JOIN FETCH i.host
    WHERE i.id IN :ids
    AND (
        i.invitationDate > :startDate 
        OR (i.invitationDate = :startDate AND i.startTime >= :nowTime)
    )
    AND i.invitationDate <= :endDate
    ORDER BY i.invitationDate ASC, i.startTime ASC
    """)
    fun findAllByIdInAndDateRange(
        @Param("ids") ids: List<Long>,
        @Param("startDate") startDate: LocalDate,
        @Param("nowTime") nowTime: LocalTime,
        @Param("endDate") endDate: LocalDate,
        pageable: Pageable
    ): Page<Invitation>

    fun findByIdAndHostId(id: Long, hostId: Long): Invitation?

    @Query("""
        SELECT i FROM Invitation i 
        WHERE i.id IN :ids 
        AND (
            (:status = 'UPCOMING' AND (i.invitationDate > CURRENT_DATE OR (i.invitationDate = CURRENT_DATE AND i.startTime >= CURRENT_TIME)))
            OR 
            (:status = 'PAST' AND (i.invitationDate < CURRENT_DATE OR (i.invitationDate = CURRENT_DATE AND i.startTime < CURRENT_TIME)))
            OR
            (:status = 'ALL')
        )
    """)
    fun findAllByIdInWithStatus(
        @Param("ids") ids: List<Long>,
        @Param("status") status: String,
        pageable: Pageable
    ): Page<Invitation>
}
