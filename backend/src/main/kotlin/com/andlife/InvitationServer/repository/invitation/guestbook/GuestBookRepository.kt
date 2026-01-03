package com.andlife.InvitationServer.repository.invitation.guestbook

import com.andlife.InvitationServer.entity.GuestBook
import org.springframework.data.jpa.repository.JpaRepository

interface GuestBookRepository: JpaRepository<GuestBook, Long>