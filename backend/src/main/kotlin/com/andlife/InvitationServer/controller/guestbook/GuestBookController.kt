package com.andlife.InvitationServer.controller.guestbook

import com.andlife.InvitationServer.auth.AuthContext
import com.andlife.InvitationServer.request.invitation.guestbook.UpdateGuestBookRequest
import com.andlife.InvitationServer.response.BaseResponse
import com.andlife.InvitationServer.response.invitation.guestbook.GuestBookResponse
import com.andlife.InvitationServer.service.invitation.guestbook.GuestBookService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/guestbooks")
class GuestBookController(
    private val guestBookService: GuestBookService,
) {
    @PutMapping("/{guestBookId}")
    fun updateGuestBook(
        @PathVariable guestBookId: Long,
        authContext: AuthContext,
        @RequestBody request: UpdateGuestBookRequest
    ): BaseResponse<GuestBookResponse> {
        val result = guestBookService.updateGuestBook(guestBookId, request, authContext)
        return BaseResponse.success(result)
    }

    @DeleteMapping("/{guestBookId}")
    fun deleteGuestBook(
        @PathVariable guestBookId: Long,
        authContext: AuthContext,
    ): BaseResponse<Long> {
        guestBookService.deleteGuestBook(guestBookId, authContext)
        return BaseResponse.success(guestBookId)
    }
}