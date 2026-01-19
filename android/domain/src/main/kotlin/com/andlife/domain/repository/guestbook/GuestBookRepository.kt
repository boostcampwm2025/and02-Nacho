package com.andlife.domain.repository.guestbook

import com.andlife.domain.error.DataError
import com.andlife.domain.model.guestbook.GalleryMedia
import com.andlife.domain.model.guestbook.GuestBook
import com.andlife.domain.model.guestbook.GuestBookMedia
import com.andlife.domain.util.Result

interface GuestBookRepository {
    suspend fun getMediaCollection(invitationId: Long): Result<List<GalleryMedia>, DataError>

    suspend fun getGuestBooksByInvitationId(invitationId: Long): Result<List<GuestBook>, DataError>

    suspend fun createGuestBook(
        invitationId: Long,
        userId: Long,
        textContent: String,
        medias: List<GuestBookMedia>,
    ): Result<GuestBook, DataError>
}
