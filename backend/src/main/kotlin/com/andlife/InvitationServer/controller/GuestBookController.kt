package com.andlife.InvitationServer.controller

import com.andlife.InvitationServer.request.invitation.guestbook.CreateGuestBookRequest
import com.andlife.InvitationServer.response.invitation.guestbook.GuestBookResponse
import com.andlife.InvitationServer.response.BaseResponse
import com.andlife.InvitationServer.response.CommonResponseCode
import com.andlife.InvitationServer.service.invitation.guestbook.GuestBookService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/invitations/guestbook")
class GuestBookController(
    private val guestBookService: GuestBookService
) {
    @PostMapping
    fun createGuestBook(
        @RequestBody request: CreateGuestBookRequest
    ): BaseResponse<GuestBookResponse> {
        return try {
            val guestBookResponse = guestBookService.createGuestBook(request)
            BaseResponse.success(guestBookResponse)
        } catch (e: IllegalArgumentException) {
            BaseResponse.success(
                responseCode = CommonResponseCode.BAD_REQUEST,
            )
        } catch (e: Exception) {
            BaseResponse.success(
                responseCode = CommonResponseCode.INTERNAL_SERVER_ERROR,
            )
        }
    }
}