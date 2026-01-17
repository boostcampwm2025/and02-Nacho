package com.andlife.InvitationServer.repository.invitation

import com.andlife.InvitationServer.entity.Invitation
import org.springframework.data.jpa.repository.JpaRepository

interface InvitationRepository : JpaRepository<Invitation, Long>