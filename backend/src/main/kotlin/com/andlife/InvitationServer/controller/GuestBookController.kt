package com.andlife.InvitationServer.controller

import com.andlife.InvitationServer.response.BaseResponse
import com.andlife.InvitationServer.service.invitation.guestbook.GuestBookService
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
    val author: GuestBookAuthorResponse,
    val invitation: GuestBookInvitationResponse,
    val textContent: String,
    val visualMedias: List<GuestBookEntryMediaResponse>,
    val audioMedias: List<GuestBookEntryMediaResponse>,
    val totalVisualCount: Int,
    val isAuthorSelf: Boolean,
    val createdAt: LocalDateTime,
)

data class GuestBookAuthorResponse(
    val id: Long,
    val name: String,
    val profileImageUrl: String?,
)

data class GuestBookInvitationResponse(
    val id: Long,
    val title: String,
)

data class GuestBookEntryMediaResponse(
    val type: MediaType,
    val url: String,
    val thumbnailUrl: String? = null,
    val durationSeconds: Int? = null,
    val displayOrder: Int,
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