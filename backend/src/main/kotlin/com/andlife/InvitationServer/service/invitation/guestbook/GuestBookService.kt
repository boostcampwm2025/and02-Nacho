package com.andlife.InvitationServer.service.invitation.guestbook

import com.andlife.InvitationServer.repository.invitation.guestbook.GuestBookRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GuestBookService(
    private val guestBookRepository: GuestBookRepository
) {

}