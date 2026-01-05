package com.andlife.InvitationServer.repository

import com.andlife.InvitationServer.entity.GuestBook
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface GuestBookRepository : JpaRepository<GuestBook, Long> {
//    @Query("""
//        SELECT gb FROM GuestBook gb
//        JOIN FETCH gb.user
//        LEFT JOIN FETCH gb.images
//        LEFT JOIN FETCH gb.audios
//        LEFT JOIN FETCH gb.videos
//        WHERE gb.invitation.id = :invitationId
//        ORDER BY gb.createdAt DESC
//    """)
//    fun findAllByInvitationId(invitationId: Long): List<GuestBook>

    @Query("""
        SELECT gb FROM GuestBook gb 
        JOIN FETCH gb.user
        WHERE gb.invitation.id = :invitationId 
        ORDER BY gb.createdAt DESC
    """)
    fun findAllByInvitationId(invitationId: Long): List<GuestBook>
}