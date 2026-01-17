package com.andlife.InvitationServer.controller.invitation

import com.andlife.InvitationServer.response.BaseResponse
import com.andlife.InvitationServer.response.invitation.InvitationResponse
import com.andlife.InvitationServer.response.invitation.guestbook.CollectionResponse
import com.andlife.InvitationServer.response.invitation.guestbook.GuestBookResponse
import com.andlife.InvitationServer.service.invitation.InvitationService
import com.andlife.InvitationServer.service.invitation.guestbook.GuestBookService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/invitations")
class InvitationController(
    private val invitationService: InvitationService,
    private val guestBookService: GuestBookService,
) {

    @GetMapping("/{invitationId}")
    fun getInvitation(
        @PathVariable invitationId: Long
    ): BaseResponse<InvitationResponse> {
        val result = invitationService.getInvitation(invitationId)
        return BaseResponse.success(result)
    }

    @GetMapping("/{invitationId}/collection")
    fun getMediaCollection(
        @PathVariable invitationId: Long
    ): BaseResponse<List<CollectionResponse>> {
        val result = guestBookService.getCollectionByInvitation(invitationId)
        return BaseResponse.success(result)
    }

    @GetMapping("/{invitationId}/guestbooks")
    fun getGuestBooks(@PathVariable invitationId: Long): BaseResponse<List<GuestBookResponse>> {
        val result = guestBookService.getGuestBooks(invitationId)

        return BaseResponse.success(result)
    }
}