package com.andlife.InvitationServer.controller

import com.andlife.InvitationServer.response.BaseResponse
import com.andlife.InvitationServer.service.GuestBookService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

enum class MediaType {
    IMAGE,
    AUDIO,
    VIDEO,
}

data class GuestBookResponse(
    val id: Long,
    val writerName: String,
    val writerProfileImage: String?,
    val invitationTitle: String,
    val textContent: String,
    val visualMedias: List<MediaResponse>,
    val audioMedias: List<MediaResponse>,
    val totalVisualCount: Int,
    val createdAt: LocalDateTime,
)

data class MediaResponse(
    val type: MediaType,
    val url: String,
    val thumbnailUrl: String? = null,
    val durationSeconds: Int? = null,
    val displayOrder: Int
)

@RestController
@RequestMapping("/api/v1/invitations")
class GuestBookController(
    private val guestBookService: GuestBookService
) {
    @GetMapping("/{invitationId}/guestbooks")
    fun getGuestBooks(@PathVariable invitationId: Long): BaseResponse<List<GuestBookResponse>> {
        val result = guestBookService.getGuestBooks(invitationId)

        return BaseResponse.success(result)
    }
}