package com.andlife.InvitationServer.repository.invitation.guestbook

import com.andlife.InvitationServer.entity.GuestBook
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface GuestBookRepository : JpaRepository<GuestBook, Long> {
    @Query("SELECT DISTINCT g FROM GuestBook g " +
            "JOIN FETCH g.user " +
            "WHERE g.invitation.id = :invitationId")
    fun findAllByInvitationIdWithDetails(@Param("invitationId") invitationId: Long): List<GuestBook>

    @Query("""
        SELECT gb FROM GuestBook gb 
        JOIN FETCH gb.user
        JOIN FETCH gb.invitation
        WHERE gb.invitation.id = :invitationId
        ORDER BY gb.createdAt DESC, gb.id DESC
    """)
    fun findAllByInvitationId(invitationId: Long, pageable: Pageable): Page<GuestBook>

    @Query(
        value = """
        SELECT DISTINCT gb FROM GuestBook gb
        JOIN FETCH gb.user u
        JOIN FETCH gb.invitation i
        JOIN FETCH i.host h
        LEFT JOIN InvitationParticipant ip ON i.id = ip.invitation.id
        WHERE (i.host.id = :userId OR ip.user.id = :userId)
        ORDER BY gb.createdAt DESC
    """,
        countQuery = """
        SELECT COUNT(DISTINCT gb) FROM GuestBook gb
        LEFT JOIN InvitationParticipant ip ON gb.invitation.id = ip.invitation.id
        WHERE (gb.invitation.host.id = :userId OR ip.user.id = :userId)
    """
    )
    fun findAllByMyRelatedInvitations(
        @Param("userId") userId: Long,
        pageable: Pageable
    ): Page<GuestBook>
}