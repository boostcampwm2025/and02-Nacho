package com.andlife.InvitationServer.service.invitation.guestbook

import com.andlife.InvitationServer.constant.MediaType
import com.andlife.InvitationServer.entity.GuestBook
import com.andlife.InvitationServer.entity.GuestBookAudio
import com.andlife.InvitationServer.entity.GuestBookImage
import com.andlife.InvitationServer.entity.GuestBookVideo
import com.andlife.InvitationServer.repository.invitation.InvitationRepository
import com.andlife.InvitationServer.repository.invitation.guestbook.GuestBookRepository
import com.andlife.InvitationServer.repository.user.UserRepository
import com.andlife.InvitationServer.request.invitation.guestbook.GuestBookRequest
import com.andlife.InvitationServer.response.AuthorResponse
import com.andlife.InvitationServer.response.PagingMetaResponse
import com.andlife.InvitationServer.response.PagingResponse
import com.andlife.InvitationServer.response.invitation.guestbook.CollectionResponse
import com.andlife.InvitationServer.response.invitation.guestbook.GuestBookInvitationResponse
import com.andlife.InvitationServer.response.invitation.guestbook.GuestBookMediaResponse
import com.andlife.InvitationServer.response.invitation.guestbook.GuestBookResponse
import com.andlife.InvitationServer.response.invitation.guestbook.toGuestBookResponse
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GuestBookService(
    private val guestBookRepository: GuestBookRepository,
    private val userRepository: UserRepository,
    private val invitationRepository: InvitationRepository
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
                collection.add(
                    CollectionResponse(
                        id = image.id,
                        mediaType = MediaType.IMAGE,
                        mediaUrl = image.imageUrl,
                        author = author,
                        content = gb.textContent,
                        createdAt = createdAt,
                        durationSeconds = 0
                    )
                )
            }

            gb.audios.forEach { audio ->
                collection.add(
                    CollectionResponse(
                        id = audio.id,
                        mediaType = MediaType.AUDIO,
                        mediaUrl = audio.audioUrl,
                        author = author,
                        content = gb.textContent,
                        createdAt = createdAt,
                        durationSeconds = audio.durationSeconds
                    )
                )
            }

            gb.videos.forEach { video ->
                collection.add(
                    CollectionResponse(
                        id = video.id,
                        mediaType = MediaType.VIDEO,
                        mediaUrl = video.videoUrl,
                        thumbnailUrl = video.thumbnailUrl,
                        author = author,
                        content = gb.textContent,
                        createdAt = createdAt,
                        durationSeconds = video.durationSeconds
                    )
                )
            }
        }

        return collection.sortedByDescending { it.createdAt }
    }

    fun getGuestBooks(invitationId: Long, pageable: Pageable): PagingResponse<GuestBookResponse> {
        val guestBooksPage = guestBookRepository.findAllByInvitationId(invitationId, pageable)

        val responsePage = guestBooksPage.map { guestBook ->
            val allMedia = mutableListOf<GuestBookMediaResponse>()

            guestBook.images.forEach {
                allMedia.add(GuestBookMediaResponse(it.id, MediaType.IMAGE, it.imageUrl, null, null, it.displayOrder))
            }
            guestBook.videos.forEach {
                allMedia.add(
                    GuestBookMediaResponse(
                        it.id,
                        MediaType.VIDEO,
                        it.videoUrl,
                        it.thumbnailUrl,
                        it.durationSeconds,
                        it.displayOrder
                    )
                )
            }
            guestBook.audios.forEach {
                allMedia.add(
                    GuestBookMediaResponse(
                        it.id,
                        MediaType.AUDIO,
                        it.audioUrl,
                        null,
                        it.durationSeconds,
                        it.displayOrder
                    )
                )
            }

            val sortedList = allMedia.sortedBy { it.displayOrder }
            val visualMedias = sortedList.filter { it.type != MediaType.AUDIO }
            val audioMedias = sortedList.filter { it.type == MediaType.AUDIO }

            GuestBookResponse(
                id = guestBook.id,
                author = AuthorResponse(
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
                isOwner = false,
                createdAt = guestBook.createdAt,
                updatedAt = guestBook.updatedAt
            )
        }

        return PagingResponse(
            meta = PagingMetaResponse(
                isEnd = !responsePage.hasNext(),
                pageableCount = responsePage.numberOfElements,
                totalCount = responsePage.totalElements,
                currentPage = responsePage.number + 1
            ),
            content = responsePage.content
        )
    }

    @Transactional
    fun createGuestBook(invitationId: Long, request: GuestBookRequest): GuestBookResponse {
        val user = userRepository.findById(request.userId)
            .orElseThrow { IllegalArgumentException("User not found with id: ${request.userId}") }

        val invitation = invitationRepository.findById(invitationId)
            .orElseThrow { IllegalArgumentException("Invitation not found with id: $invitationId") }

        val guestBook = GuestBook(
            invitation = invitation,
            user = user,
            textContent = request.textContent
        )

        request.medias.forEach { mediaReq ->
            when (mediaReq.mediaType) {
                "IMAGE" -> {
                    val image = GuestBookImage(
                        guestBook = guestBook,
                        imageUrl = mediaReq.mediaUrl,
                        displayOrder = mediaReq.displayOrder
                    )
                    guestBook.images.add(image)
                }

                "AUDIO" -> {
                    val audio = GuestBookAudio(
                        guestBook = guestBook,
                        audioUrl = mediaReq.mediaUrl,
                        durationSeconds = mediaReq.durationSeconds ?: 0,
                        displayOrder = mediaReq.displayOrder
                    )
                    guestBook.audios.add(audio)
                }

                "VIDEO" -> {
                    val video = GuestBookVideo(
                        guestBook = guestBook,
                        videoUrl = mediaReq.mediaUrl,
                        thumbnailUrl = mediaReq.thumbnailUrl ?: "",
                        durationSeconds = mediaReq.durationSeconds ?: 0,
                        displayOrder = mediaReq.displayOrder
                    )
                    guestBook.videos.add(video)
                }

                else -> throw IllegalArgumentException("Unsupported media type: ${mediaReq.mediaType}")
            }
        }

        val savedGuestBook = guestBookRepository.save(guestBook)
        return savedGuestBook.toGuestBookResponse()
    }
}