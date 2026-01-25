package com.andlife.nachoserver.service.guestbook

import com.andlife.nachoserver.auth.AuthContext
import com.andlife.nachoserver.constant.MediaType
import com.andlife.nachoserver.entity.*
import com.andlife.nachoserver.error.BusinessException
import com.andlife.nachoserver.repository.invitation.InvitationRepository
import com.andlife.nachoserver.repository.guestbook.GuestBookRepository
import com.andlife.nachoserver.repository.user.UserRepository
import com.andlife.nachoserver.request.guestbook.GuestBookRequest
import com.andlife.nachoserver.request.guestbook.UpdateGuestBookRequest
import com.andlife.nachoserver.response.AuthorResponse
import com.andlife.nachoserver.response.CommonResponseCode
import com.andlife.nachoserver.response.PagingMetaResponse
import com.andlife.nachoserver.response.PagingResponse
import com.andlife.nachoserver.response.guestbook.CollectionResponse
import com.andlife.nachoserver.response.guestbook.GuestBookInvitationResponse
import com.andlife.nachoserver.response.guestbook.GuestBookMediaResponse
import com.andlife.nachoserver.response.guestbook.GuestBookResponse
import com.andlife.nachoserver.response.guestbook.toGuestBookResponse
import com.andlife.nachoserver.service.media.MediaService
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GuestBookService(
    private val guestBookRepository: GuestBookRepository,
    private val userRepository: UserRepository,
    private val invitationRepository: InvitationRepository,
    private val mediaService: MediaService
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

    fun getGuestBooks(
        invitationId: Long,
        pageable: Pageable,
        authContext: AuthContext
    ): PagingResponse<GuestBookResponse> {
        val guestBooksPage = guestBookRepository.findAllByInvitationId(invitationId, pageable)

        val responsePage = guestBooksPage.map { guestBook ->
            val isOwner = when (authContext) {
                is AuthContext.Member -> authContext.userId == guestBook.user.id
                is AuthContext.Guest -> false
            }

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
                invitation = null,
                textContent = guestBook.textContent,
                visualMedias = visualMedias,
                audioMedias = audioMedias,
                totalVisualCount = visualMedias.size,
                isOwner = isOwner,
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

                    // 썸네일이 있는 경우 VideoPreviewThumbnail에도 추가
                    if (!mediaReq.thumbnailUrl.isNullOrEmpty()) {
                        val previewThumbnail = VideoPreviewThumbnail(
                            video = video,
                            thumbnailUrl = mediaReq.thumbnailUrl,
                            timeSeconds = 1.0 // 1초 지점의 썸네일: 이후 규칙이 변경되면 해당 부분도 수정 필요
                        )
                        video.previewThumbnails.add(previewThumbnail)
                    }

                    guestBook.videos.add(video)
                }

                else -> throw IllegalArgumentException("Unsupported media type: ${mediaReq.mediaType}")
            }
        }

        val savedGuestBook = guestBookRepository.save(guestBook)
        return savedGuestBook.toGuestBookResponse()
    }

    @Transactional
    fun updateGuestBook(
        guestBookId: Long,
        request: UpdateGuestBookRequest,
        authContext: AuthContext
    ): GuestBookResponse {
        val guestBook = guestBookRepository.findById(guestBookId)
            .orElseThrow { IllegalArgumentException("방명록을 찾을 수 없습니다. id: $guestBookId") }

        validateOwner(guestBook.user.id, authContext)

        val mediaKeysToDelete = getAllMediaKeys(guestBook).toMutableList()

        guestBook.textContent = request.textContent
        guestBook.images.removeIf { !request.existingImageIds.contains(it.id) }
        guestBook.audios.removeIf { !request.existingAudioIds.contains(it.id) }
        guestBook.videos.removeIf { !request.existingVideoIds.contains(it.id) }

        request.newMedias.forEach { mediaReq ->
            when (mediaReq.mediaType) {
                "IMAGE" -> guestBook.images.add(
                    GuestBookImage(
                        guestBook = guestBook,
                        imageUrl = mediaReq.mediaUrl,
                        displayOrder = mediaReq.displayOrder
                    )
                )

                "AUDIO" -> guestBook.audios.add(
                    GuestBookAudio(
                        guestBook = guestBook,
                        audioUrl = mediaReq.mediaUrl,
                        durationSeconds = mediaReq.durationSeconds ?: 0,
                        displayOrder = mediaReq.displayOrder
                    )
                )

                "VIDEO" -> guestBook.videos.add(
                    GuestBookVideo(
                        guestBook = guestBook,
                        videoUrl = mediaReq.mediaUrl,
                        thumbnailUrl = mediaReq.thumbnailUrl ?: "",
                        durationSeconds = mediaReq.durationSeconds ?: 0,
                        displayOrder = mediaReq.displayOrder
                    )
                )
            }
        }

        mediaKeysToDelete.forEach { mediaKey ->
            mediaService.deleteMedia(mediaKey)
        }

        return guestBook.toGuestBookResponse()
    }

    @Transactional
    fun deleteGuestBook(guestBookId: Long, authContext: AuthContext) {
        val guestBook = guestBookRepository.findByIdOrNull(guestBookId)
            ?: throw EntityNotFoundException("방명록을 찾을 수 없습니다. id: $guestBookId")

        validateOwner(guestBook.user.id, authContext)

        val mediaKeys = getAllMediaKeys(guestBook)
        guestBookRepository.delete(guestBook)

        mediaKeys.forEach { mediaKey ->
            mediaService.deleteMedia(mediaKey)
        }
    }

    private fun validateOwner(guestBookUserId: Long, authContext: AuthContext) {
        when (authContext) {
            is AuthContext.Member -> {
                if (guestBookUserId != authContext.userId) {
                    throw BusinessException(CommonResponseCode.FORBIDDEN)
                }
            }

            is AuthContext.Guest -> {
                throw BusinessException(CommonResponseCode.FORBIDDEN)
            }
        }
    }

    private fun getAllMediaKeys(guestBook: GuestBook): List<String> {
        val keys = mutableListOf<String>()
        guestBook.images.forEach { keys.add(mediaService.extractKey(it.imageUrl)) }
        guestBook.audios.forEach { keys.add(mediaService.extractKey(it.audioUrl)) }
        guestBook.videos.forEach {
            keys.add(mediaService.extractKey(it.videoUrl))
            keys.add(mediaService.extractKey(it.thumbnailUrl))
        }
        return keys
    }

    fun getAllRelatedGuestBooks(
        authContext: AuthContext,
        pageable: Pageable
    ): PagingResponse<GuestBookResponse> {

        val guestBooksPage = when (authContext) {
            is AuthContext.Member -> {
                guestBookRepository.findAllByMyRelatedInvitations(authContext.userId, pageable)
            }
            is AuthContext.Guest -> {
                Page.empty(pageable) //TODO: 비로그인 유저 임시 빈값 조회
            }
        }

        val responsePage = guestBooksPage.map { guestBook ->
            val allMedia = mutableListOf<GuestBookMediaResponse>().apply {
                addAll(guestBook.images.map { GuestBookMediaResponse(it.id, MediaType.IMAGE, it.imageUrl, null, null, it.displayOrder) })
                addAll(guestBook.videos.map { GuestBookMediaResponse(it.id, MediaType.VIDEO, it.videoUrl, it.thumbnailUrl, it.durationSeconds, it.displayOrder) })
                addAll(guestBook.audios.map { GuestBookMediaResponse(it.id, MediaType.AUDIO, it.audioUrl, null, it.durationSeconds, it.displayOrder) })
            }.sortedBy { it.displayOrder }

            val (audioMedias, visualMedias) = allMedia.partition { it.type == MediaType.AUDIO }
            val isOwner = (authContext is AuthContext.Member && guestBook.user.id == authContext.userId)

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
                isOwner = isOwner,
                createdAt = guestBook.createdAt,
                updatedAt = guestBook.updatedAt
            )
        }

        return PagingResponse(
            meta = PagingMetaResponse(
                isEnd = !guestBooksPage.hasNext(),
                pageableCount = guestBooksPage.numberOfElements,
                totalCount = guestBooksPage.totalElements,
                currentPage = guestBooksPage.number + 1
            ),
            content = responsePage.content
        )
    }
}