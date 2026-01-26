package com.andlife.nachoserver.repository.invitation

import com.andlife.nachoserver.entity.InvitationCard
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface InvitationCardRepository : JpaRepository<InvitationCard, Long> {
    @Query("""
        SELECT ic FROM InvitationCard ic
        JOIN FETCH ic.invitation
        WHERE ic.invitation.id = :invitationId
    """)
    fun findByInvitationIdWithDetails(@Param("invitationId") invitationId: Long): InvitationCard?
}