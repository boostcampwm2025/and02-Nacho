package com.andlife.InvitationServer.service

import com.andlife.InvitationServer.controller.GuestBookResponse
import com.andlife.InvitationServer.controller.MediaResponse
import com.andlife.InvitationServer.controller.MediaType
import com.andlife.InvitationServer.repository.GuestBookRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GuestBookService(
    private val guestBookRepository: GuestBookRepository
) {
    fun getGuestBooks(invitationId: Long): List<GuestBookResponse> {
        val guestBooks = guestBookRepository.findAllByInvitationId(invitationId)

        return guestBooks.map { guestBook ->
            val allMedia = mutableListOf<MediaResponse>()

            guestBook.images.forEach {
                allMedia.add(MediaResponse(MediaType.IMAGE, it.imageUrl, null, null, it.displayOrder))
            }
            guestBook.videos.forEach {
                allMedia.add(MediaResponse(MediaType.VIDEO, it.videoUrl, it.thumbnailUrl, it.durationSeconds, it.displayOrder))
            }
            guestBook.audios.forEach {
                allMedia.add(MediaResponse(MediaType.AUDIO, it.audioUrl, null, it.durationSeconds, it.displayOrder))
            }

            val sortedList = allMedia.sortedBy { it.displayOrder }

            val visualMedias = sortedList.filter { it.type != MediaType.AUDIO }
            val audioMedias = sortedList.filter { it.type == MediaType.AUDIO }

            GuestBookResponse(
                id = guestBook.id,
                writerName = guestBook.user.name,
                writerProfileImage = guestBook.user.profileImageUrl,
                invitationTitle = guestBook.invitation.title,
                textContent = guestBook.textContent,
                visualMedias = visualMedias,
                audioMedias = audioMedias,
                totalVisualCount = visualMedias.size,
                createdAt = guestBook.createdAt
            )
        }
    }
}