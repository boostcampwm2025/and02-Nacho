package com.andlife.InvitationServer.repository.invitation.participant

import com.andlife.InvitationServer.entity.InvitationParticipant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface InvitationParticipantRepository : JpaRepository<InvitationParticipant, Long> {

    @Query("SELECT p FROM InvitationParticipant p JOIN FETCH p.invitation WHERE p.user.id = :userId")
    fun findAllByUserIdWithInvitation(@Param("userId") userId: Long): List<InvitationParticipant>
}