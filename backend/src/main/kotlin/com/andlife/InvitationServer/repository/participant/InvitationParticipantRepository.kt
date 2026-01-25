package com.andlife.InvitationServer.repository.participant

import com.andlife.InvitationServer.entity.InvitationParticipant
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate
import java.time.LocalTime

interface InvitationParticipantRepository : JpaRepository<InvitationParticipant, Long> {

    // 전체 목록
    @Query(
        value = "SELECT p FROM InvitationParticipant p JOIN FETCH p.invitation WHERE p.user.id = :userId",
        countQuery = "SELECT count(p) FROM InvitationParticipant p WHERE p.user.id = :userId"
    )
    fun findAllByUserIdWithInvitation(@Param("userId") userId: Long, pageable: Pageable): Page<InvitationParticipant>

    // 다가오는 초대장
    @Query(
        value = """
            SELECT p FROM InvitationParticipant p 
            JOIN FETCH p.invitation 
            WHERE p.user.id = :userId 
            AND (p.invitation.invitationDate > :nowDate 
                OR (p.invitation.invitationDate = :nowDate AND p.invitation.startTime >= :nowTime))
        """,
        countQuery = """
            SELECT count(p) FROM InvitationParticipant p 
            WHERE p.user.id = :userId 
            AND (p.invitation.invitationDate > :nowDate 
                OR (p.invitation.invitationDate = :nowDate AND p.invitation.startTime >= :nowTime))
        """
    )
    fun findUpcomingInvitations(
        @Param("userId") userId: Long,
        @Param("nowDate") nowDate: LocalDate,
        @Param("nowTime") nowTime: LocalTime,
        pageable: Pageable
    ): Page<InvitationParticipant>

    // 지난 초대장
    @Query(
        value = """
            SELECT p FROM InvitationParticipant p 
            JOIN FETCH p.invitation 
            WHERE p.user.id = :userId 
            AND (p.invitation.invitationDate < :nowDate 
                OR (p.invitation.invitationDate = :nowDate AND p.invitation.startTime < :nowTime))
        """,
        countQuery = """
            SELECT count(p) FROM InvitationParticipant p 
            WHERE p.user.id = :userId 
            AND (p.invitation.invitationDate < :nowDate 
                OR (p.invitation.invitationDate = :nowDate AND p.invitation.startTime < :nowTime))
        """
    )
    fun findPastInvitations(
        @Param("userId") userId: Long,
        @Param("nowDate") nowDate: LocalDate,
        @Param("nowTime") nowTime: LocalTime,
        pageable: Pageable
    ): Page<InvitationParticipant>
}