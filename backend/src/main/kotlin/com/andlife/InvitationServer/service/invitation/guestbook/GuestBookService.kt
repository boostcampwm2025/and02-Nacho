package com.andlife.InvitationServer.service.invitation.guestbook

import com.andlife.InvitationServer.constant.MediaType
import com.andlife.InvitationServer.repository.invitation.guestbook.GuestBookRepository
import com.andlife.InvitationServer.response.AuthorResponse
import com.andlife.InvitationServer.response.invitation.guestbook.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GuestBookService(
    private val guestBookRepository: GuestBookRepository
) {

    fun getCollectionByInvitation(invitationId: Long): List<CollectionResponse> {
        val guestBooks = guestBookRepository.findAllByInvitationIdWithDetails(invitationId)
        val collection = mutableListOf<CollectionResponse>()

        guestBooks.forEach { gb ->
            val author = AuthorResponse(
                id = gb.user.id,
                name = gb.user.name,
                profileImageUrl = gb.user.profileImageUrl
            )
            val createdAt = gb.createdAt

            gb.images.forEach { image ->
                collection.add(CollectionResponse(
                    id = image.id,
                    mediaType = MediaType.IMAGE,
                    mediaUrl = image.imageUrl,
                    author = author,
                    content = gb.textContent,
                    createdAt = createdAt,
                    durationSeconds = 0
                ))
            }

            gb.audios.forEach { audio ->
                collection.add(CollectionResponse(
                    id = audio.id,
                    mediaType = MediaType.AUDIO,
                    mediaUrl = audio.audioUrl,
                    author = author,
                    content = gb.textContent,
                    createdAt = createdAt,
                    durationSeconds = audio.durationSeconds
                ))
            }

            gb.videos.forEach { video ->
                collection.add(CollectionResponse(
                    id = video.id,
                    mediaType = MediaType.VIDEO,
                    mediaUrl = video.thumbnailUrl,
                    author = author,
                    content = gb.textContent,
                    createdAt = createdAt,
                    durationSeconds = video.durationSeconds
                ))
            }
        }

        return collection.sortedByDescending { it.createdAt }
    }

    fun getGuestBooks(invitationId: Long): List<GuestBookResponse> {
        val guestBooks = guestBookRepository.findAllByInvitationId(invitationId)

        return guestBooks.map { guestBook ->
            val allMedia = mutableListOf<GuestBookMediaResponse>()

            guestBook.images.forEach {
                allMedia.add(GuestBookMediaResponse(it.id, MediaType.IMAGE, it.imageUrl, null, null, it.displayOrder))
            }
            guestBook.videos.forEach {
                allMedia.add(GuestBookMediaResponse(it.id, MediaType.VIDEO, it.videoUrl, it.thumbnailUrl, it.durationSeconds, it.displayOrder))
            }
            guestBook.audios.forEach {
                allMedia.add(GuestBookMediaResponse(it.id, MediaType.AUDIO, it.audioUrl, null, it.durationSeconds, it.displayOrder))
            }

            val sortedList = allMedia.sortedBy { it.displayOrder }

            val visualMedias = sortedList.filter { it.type != MediaType.AUDIO }
            val audioMedias = sortedList.filter { it.type == MediaType.AUDIO }

            GuestBookResponse(
                id = guestBook.id,
                author = GuestBookAuthorResponse(
                    id = guestBook.user.id,
                    name = guestBook.user.name,
                    profileImageUrl = guestBook.user.profileImageUrl
                ),
                invitation = GuestBookInvitationResponse(
                    id = guestBook.invitation.id,
                    title = guestBook.invitation.title
                ),
                textContent = guestBook.textContent,
                visualMedias = visualMedias,
                audioMedias = audioMedias,
                totalVisualCount = visualMedias.size,
                isAuthorSelf = false, // TODO: 인증 기능 구현 후 수정 필요
                createdAt = guestBook.createdAt
            )
        }
    }
}