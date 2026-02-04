package com.andlife.nachoserver.controller.invitation

import com.andlife.nachoserver.request.invitation.CreateInvitationRequest
import com.andlife.nachoserver.auth.AuthContext
import com.andlife.nachoserver.request.invitation.InvitationCardRequest
import com.andlife.nachoserver.request.guestbook.GuestBookRequest
import com.andlife.nachoserver.request.invitation.UpdateInvitationRequest
import com.andlife.nachoserver.response.BaseResponse
import com.andlife.nachoserver.response.CommonResponseCode
import com.andlife.nachoserver.response.PagingResponse
import com.andlife.nachoserver.response.invitation.InvitationResponse
import com.andlife.nachoserver.response.invitation.InvitationSummaryResponse
import com.andlife.nachoserver.response.invitation.UpcomingInvitationResponse
import com.andlife.nachoserver.response.guestbook.CollectionResponse
import com.andlife.nachoserver.response.guestbook.GuestBookResponse
import com.andlife.nachoserver.response.invitation.JoinResponse
import com.andlife.nachoserver.service.invitation.InvitationService
import com.andlife.nachoserver.service.guestbook.GuestBookService
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
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
    @PostMapping("/sync")
    fun syncInvitations(
        @RequestBody invitationIds: List<Long>,
        authContext: AuthContext
    ): BaseResponse<Unit> {
        return when (authContext) {
            is AuthContext.Member -> {
                invitationService.syncInvitations(authContext.userId, invitationIds)
                BaseResponse.success(Unit)
            }
            is AuthContext.Guest -> {
                BaseResponse.error(CommonResponseCode.UNAUTHORIZED)
            }
        }
    }

    @PostMapping("/{invitationId}/join")
    fun joinInvitation(
        @PathVariable invitationId: Long,
        authContext: AuthContext
    ): BaseResponse<JoinResponse> {
        val result = when (authContext) {
            is AuthContext.Member -> {
                println(">>> [멤버 진입] UserID: ${authContext.userId}")
                invitationService.joinInvitation(
                    invitationId = invitationId,
                    userId = authContext.userId,
                    guestInvitationIds = emptyList()
                )
            }
            is AuthContext.Guest -> {
                println(">>> [게스트 진입] 초대장 목록: ${authContext.invitationIds}")
                invitationService.joinInvitation(
                    invitationId = invitationId,
                    userId = null,
                    guestInvitationIds = authContext.invitationIds
                )
            }
        }
        return BaseResponse.success(result)
    }

    @GetMapping("/joined")
    fun getParticipantInvitations(
        authContext: AuthContext,
        @RequestParam(required = false, defaultValue = "UPCOMING") status: String,
        @RequestParam(required = false, defaultValue = "ASC") sortType: String,
        @PageableDefault(size = 10) pageable: Pageable
    ): BaseResponse<PagingResponse<InvitationSummaryResponse>> {
        val result = when (authContext) {
            is AuthContext.Member -> {
                invitationService.getParticipantInvitations(
                    userId = authContext.userId,
                    guestInvitationIds = emptyList(),
                    status = status,
                    sortType = sortType,
                    pageable = pageable
                )
            }
            is AuthContext.Guest -> {
                println(">>> [게스트 진입] 초대장 목록: ${authContext.invitationIds}")
                invitationService.getParticipantInvitations(
                    userId = null,
                    guestInvitationIds = authContext.invitationIds,
                    status = status,
                    sortType = sortType,
                    pageable = pageable
                )
            }
        }
        return BaseResponse.success(result)
    }

    @PostMapping("/{invitationId}/leave")
    fun leaveInvitation(
        @PathVariable invitationId: Long,
        authContext: AuthContext
    ): BaseResponse<Unit> {
        when (authContext) {
            is AuthContext.Member -> {
                invitationService.leaveInvitation(invitationId, authContext.userId)
            }
            is AuthContext.Guest -> {
                invitationService.leaveInvitationForGuest(invitationId)
            }
        }
        return BaseResponse.success(Unit)
    }

    @GetMapping("/mine")
    fun getMyInvitations(
        authContext: AuthContext,
        @RequestParam(required = false, defaultValue = "UPCOMING") status: String,
        @RequestParam(required = false, defaultValue = "ASC") sortType: String,
        @PageableDefault(size = 10) pageable: Pageable
    ): BaseResponse<PagingResponse<InvitationSummaryResponse>> {
        return when (authContext) {
            is AuthContext.Member -> {
                val result = invitationService.getMyInvitations(authContext.userId, status, sortType, pageable)
                BaseResponse.success(result)
            }
            is AuthContext.Guest -> {
                BaseResponse.error(CommonResponseCode.UNAUTHORIZED)
            }
        }
    }

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
        authContext: AuthContext,
        @PageableDefault(size = 10, sort = ["createdAt", "id"], direction = Sort.Direction.DESC) pageable: Pageable
    ): BaseResponse<PagingResponse<GuestBookResponse>> {
        val result = guestBookService.getGuestBooks(invitationId, pageable, authContext)
        return BaseResponse.success(result)
    }

    @PostMapping("/{invitationId}/guestbooks")
    fun createGuestBook(
        @PathVariable invitationId: Long,
        authContext: AuthContext,
        @RequestBody request: GuestBookRequest
    ): BaseResponse<*> {
        return try {
            val guestBookResponse = guestBookService.createGuestBook(invitationId, request, authContext)
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

    @PostMapping("/{invitationId}/cards")
    fun createInvitationCard(
        @PathVariable invitationId: Long,
        @RequestBody request: InvitationCardRequest
    ): BaseResponse<Long> {
        return try {
            val cardId = invitationService.createInvitationCard(invitationId, request)
            BaseResponse.success(cardId)
        } catch (e: NoSuchElementException) {
            BaseResponse.error(
                responseCode = CommonResponseCode.NOT_FOUND,
                customMessage = e.message
            )
        } catch (e: IllegalStateException) {
            BaseResponse.error(
                responseCode = CommonResponseCode.BAD_REQUEST,
                customMessage = e.message
            )
        } catch (e: Exception) {
            BaseResponse.error(responseCode = CommonResponseCode.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping("/guestbooks/all")
    fun getAllRelatedGuestBooks(
        authContext: AuthContext,
        @PageableDefault(size = 10, sort = ["createdAt", "id"], direction = Sort.Direction.DESC) pageable: Pageable
    ): BaseResponse<PagingResponse<GuestBookResponse>> {
        val result = guestBookService.getAllRelatedGuestBooks(authContext, pageable)
        return BaseResponse.success(result)
    }

    @PutMapping("/cards/{cardId}")
    fun updateInvitationCard(
        @PathVariable cardId: Long,
        @RequestBody request: InvitationCardRequest
    ): BaseResponse<Long> {
        return try {
            val updatedCardId = invitationService.updateInvitationCard(cardId, request)
            BaseResponse.success(updatedCardId)
        } catch (e: NoSuchElementException) {
            BaseResponse.error(
                responseCode = CommonResponseCode.NOT_FOUND,
                customMessage = e.message
            )
        } catch (e: Exception) {
            BaseResponse.error(responseCode = CommonResponseCode.INTERNAL_SERVER_ERROR)
        }
    }

    @PostMapping
    fun createInvitation(
        authContext: AuthContext,
        @RequestBody request: CreateInvitationRequest
    ): BaseResponse<InvitationResponse> {
        if (authContext !is AuthContext.Member) {
            return BaseResponse.error(CommonResponseCode.UNAUTHORIZED)
        }

        return try {
            val response = invitationService.createInvitation(authContext.userId, request)
            BaseResponse.success(response)
        } catch (e: IllegalArgumentException) {
            println(e.message)
            BaseResponse.error(
                responseCode = CommonResponseCode.BAD_REQUEST,
                customMessage = e.message
            )
        } catch (e: NoSuchElementException) {
            println(e.message)
            BaseResponse.error(
                responseCode = CommonResponseCode.NOT_FOUND,
                customMessage = e.message
            )
        } catch (e: Exception) {
            println(e.message)
            BaseResponse.error(responseCode = CommonResponseCode.INTERNAL_SERVER_ERROR)
        }
    }

    @PutMapping("/{invitationId}")
    fun updateInvitation(
        @PathVariable invitationId: Long,
        @RequestBody request: UpdateInvitationRequest
    ): BaseResponse<InvitationResponse> {
        return try {
            val response = invitationService.updateInvitation(invitationId, request)
            BaseResponse.success(response)
        } catch (e: IllegalArgumentException) {
            println(e.message)
            BaseResponse.error(
                responseCode = CommonResponseCode.BAD_REQUEST,
                customMessage = e.message
            )
        } catch (e: NoSuchElementException) {
            println(e.message)
            BaseResponse.error(
                responseCode = CommonResponseCode.NOT_FOUND,
                customMessage = e.message
            )
        } catch (e: Exception) {
            println(e.message)
            BaseResponse.error(responseCode = CommonResponseCode.INTERNAL_SERVER_ERROR)
        }
    }

    @DeleteMapping("/{invitationId}")
    fun deleteInvitation(
        @PathVariable invitationId: Long,
        authContext: AuthContext
    ): BaseResponse<Unit> {
        return when (authContext) {
            is AuthContext.Member -> {
                invitationService.deleteInvitation(invitationId, authContext.userId)
                BaseResponse.success(Unit)
            }
            is AuthContext.Guest -> {
                BaseResponse.error(CommonResponseCode.FORBIDDEN)
            }
        }
    }

    @GetMapping("/upcoming")
    fun getUpcomingInvitations(
        authContext: AuthContext,
        @RequestParam(defaultValue = "30") days: Long,
        @PageableDefault(size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC) pageable: Pageable
    ): BaseResponse<PagingResponse<UpcomingInvitationResponse>> {
        val result = invitationService.getUpcomingInvitations(authContext, days, pageable)
        return BaseResponse.success(result)
    }
}