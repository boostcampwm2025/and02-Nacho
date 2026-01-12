package com.andlife.InvitationServer.repository.invitation.guestbook

import com.andlife.InvitationServer.entity.GuestBook
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
        WHERE gb.invitation.id = :invitationId 
        ORDER BY gb.createdAt DESC
    """)
    fun findAllByInvitationId(invitationId: Long): List<GuestBook>
}