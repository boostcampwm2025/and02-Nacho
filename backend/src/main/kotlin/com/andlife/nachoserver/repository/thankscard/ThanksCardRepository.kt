package com.andlife.nachoserver.repository.thankscard

import com.andlife.nachoserver.entity.ThanksCard
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ThanksCardRepository : JpaRepository<ThanksCard, Long> {

    @Query("""
        SELECT tc FROM ThanksCard tc
        JOIN FETCH tc.invitation
        WHERE tc.invitation.id = :invitationId
    """)
    fun findByInvitationIdWithDetails(@Param("invitationId") invitationId: Long): ThanksCard?

    @Modifying
    @Transactional
    @Query("DELETE FROM ThanksCard tc WHERE tc.invitation.id = :invitationId")
    fun deleteByInvitationId(@Param("invitationId") invitationId: Long)
}
