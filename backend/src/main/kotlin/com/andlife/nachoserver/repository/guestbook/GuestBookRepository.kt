package com.andlife.nachoserver.repository.guestbook

import com.andlife.nachoserver.entity.GuestBook
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface GuestBookRepository : JpaRepository<GuestBook, Long> {
    @Query(
        "SELECT DISTINCT g FROM GuestBook g " +
                "JOIN FETCH g.user " +
                "WHERE g.invitation.id = :invitationId"
    )
    fun findAllByInvitationIdWithDetails(@Param("invitationId") invitationId: Long): List<GuestBook>

    @Query(
        """
        SELECT gb FROM GuestBook gb 
        JOIN FETCH gb.user
        JOIN FETCH gb.invitation
        WHERE gb.invitation.id = :invitationId
    """
    )
    fun findAllByInvitationId(invitationId: Long, pageable: Pageable): Page<GuestBook>

    fun findAllByInvitationId(invitationId: Long): List<GuestBook>
    fun findAllByUserId(userId: Long): List<GuestBook>

    @Query("""
            SELECT DISTINCT gb FROM GuestBook gb
            JOIN FETCH gb.user u
            JOIN FETCH gb.invitation i
            JOIN FETCH i.host h
            LEFT JOIN InvitationParticipant ip ON i.id = ip.invitation.id
            WHERE (h.id = :userId OR ip.user.id = :userId)
    """)
    fun findAllByMyRelatedInvitations(
        @Param("userId") userId: Long,
        pageable: Pageable
    ): Page<GuestBook>

    fun findAllByInvitationIdIn(invitationIds: List<Long>, pageable: Pageable): Page<GuestBook>
}
