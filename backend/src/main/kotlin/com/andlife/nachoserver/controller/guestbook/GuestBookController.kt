package com.andlife.nachoserver.controller.guestbook

import com.andlife.nachoserver.auth.AuthContext
import com.andlife.nachoserver.request.guestbook.UpdateGuestBookRequest
import com.andlife.nachoserver.response.BaseResponse
import com.andlife.nachoserver.response.guestbook.GuestBookResponse
import com.andlife.nachoserver.service.guestbook.GuestBookService
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