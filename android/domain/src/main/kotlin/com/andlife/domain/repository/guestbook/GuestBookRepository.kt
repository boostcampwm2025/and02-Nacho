package com.andlife.domain.repository.guestbook

import androidx.paging.PagingData
import com.andlife.domain.error.DataError
import com.andlife.domain.model.guestbook.GalleryMedia
import com.andlife.domain.model.guestbook.GuestBook
import com.andlife.domain.model.guestbook.GuestBookMedia
import com.andlife.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface GuestBookRepository {
    suspend fun getMediaCollection(invitationId: Long): Result<List<GalleryMedia>, DataError>

    fun getGuestBooksByInvitationId(invitationId: Long): Flow<PagingData<GuestBook>>

    suspend fun createGuestBook(
        invitationId: Long,
        userId: Long,
        textContent: String,
        medias: List<GuestBookMedia>,
    ): Result<GuestBook, DataError>

    fun getAllRelatedGuestBooks(): Flow<PagingData<GuestBook>>
}
