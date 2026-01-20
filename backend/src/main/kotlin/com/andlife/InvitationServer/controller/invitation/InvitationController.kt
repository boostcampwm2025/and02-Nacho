package com.andlife.InvitationServer.controller.invitation

import com.andlife.InvitationServer.common.auth.AuthUser
import com.andlife.InvitationServer.request.invitation.guestbook.GuestBookRequest
import com.andlife.InvitationServer.response.BaseResponse
import com.andlife.InvitationServer.response.CommonResponseCode
import com.andlife.InvitationServer.response.PagingResponse
import com.andlife.InvitationServer.response.invitation.InvitationResponse
import com.andlife.InvitationServer.response.invitation.UpcomingInvitationResponse
import com.andlife.InvitationServer.response.invitation.guestbook.CollectionResponse
import com.andlife.InvitationServer.response.invitation.guestbook.GuestBookResponse
import com.andlife.InvitationServer.service.invitation.InvitationService
import com.andlife.InvitationServer.service.invitation.guestbook.GuestBookService
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
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
    fun getGuestBooks(
        @PathVariable invitationId: Long,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): BaseResponse<PagingResponse<GuestBookResponse>> {
        val result = guestBookService.getGuestBooks(invitationId, pageable)
        return BaseResponse.success(result)
    }

    @PostMapping("/{invitationId}/guestbooks")
    fun createGuestBook(
        @PathVariable invitationId: Long,
        @RequestBody request: GuestBookRequest
    ): BaseResponse<*> {
        return try {
            val guestBookResponse = guestBookService.createGuestBook(invitationId, request)
            BaseResponse.success(guestBookResponse)
        } catch (e: IllegalArgumentException) {
            BaseResponse.error(
                responseCode = CommonResponseCode.BAD_REQUEST,
            )
        } catch (e: Exception) {
            BaseResponse.error(
                responseCode = CommonResponseCode.INTERNAL_SERVER_ERROR,
            )
        }
    }

    @GetMapping("/upcoming")
    fun getUpcomingInvitations(
        @AuthUser userId: Long,
        @RequestParam(defaultValue = "30") days: Long,
    ): BaseResponse<List<UpcomingInvitationResponse>> {
        val result = invitationService.getUpcomingInvitations(userId, days)
        return BaseResponse.success(result)
    }
}