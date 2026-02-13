package com.andlife.nachoserver.repository.participant

import com.andlife.nachoserver.entity.InvitationParticipant
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate
import java.time.LocalTime

interface InvitationParticipantRepository : JpaRepository<InvitationParticipant, Long> {

    // 전체 목록
    fun existsByInvitationIdAndUserId(invitationId: Long, userId: Long): Boolean

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

    @Query("SELECT p FROM InvitationParticipant p WHERE p.user.id = :userId AND p.invitation.id = :invitationId")
    fun findByUserIdAndInvitationId(
        @Param("userId") userId: Long,
        @Param("invitationId") invitationId: Long
    ): InvitationParticipant?

    @Modifying
    @Transactional
    @Query("DELETE FROM InvitationParticipant p WHERE p.invitation.id = :invitationId")
    fun deleteAllByInvitationId(@Param("invitationId") invitationId: Long)

    @Modifying
    @Transactional
    @Query("DELETE FROM InvitationParticipant p WHERE p.user.id = :userId")
    fun deleteAllByUserId(@Param("userId") userId: Long)
}
