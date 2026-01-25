package com.andlife.nachoserver.repository.invitation

import com.andlife.nachoserver.entity.AnnouncementSection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface AnnouncementRepository : JpaRepository<AnnouncementSection, Long> {
    @Query("""
        SELECT a FROM AnnouncementSection a
        WHERE a.invitation.id = :invitationId
        ORDER BY a.displayOrder ASC
    """)
    fun findAllByInvitationIdOrderByDisplayOrder(@Param("invitationId") invitationId: Long): List<AnnouncementSection>
}