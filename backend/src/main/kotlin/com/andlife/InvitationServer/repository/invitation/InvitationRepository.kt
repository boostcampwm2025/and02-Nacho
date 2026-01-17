package com.andlife.InvitationServer.repository.invitation

import com.andlife.InvitationServer.entity.Invitation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface InvitationRepository : JpaRepository<Invitation, Long> {
    @Query("""
        SELECT i FROM Invitation i
        JOIN FETCH i.host
        WHERE i.id = :invitationId
    """)
    fun findByInvitationIdWithHost(@Param("invitationId") invitationId: Long): Invitation?
}
