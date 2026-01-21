package com.andlife.data.datasource.remote.invitation.guestbook

import com.andlife.domain.error.DataError
import com.andlife.domain.util.Result
import com.andlife.network.api.guestbook.GuestBookRequest
import com.andlife.network.api.guestbook.GuestBookResponse
import com.andlife.network.api.guestbook.UpdateGuestBookRequest
import com.andlife.network.model.PagingResponse
import com.andlife.network.model.invitation.guestbook.CollectionResponse

interface GuestBookRemoteDataSource {
    suspend fun getMediaCollection(invitationId: Long): Result<List<CollectionResponse>, DataError>

    suspend fun getGuestBooksByInvitationId(
        invitationId: Long,
        page: Int,
        size: Int,
    ): Result<PagingResponse<GuestBookResponse>, DataError>

    suspend fun createGuestBook(invitationId: Long, request: GuestBookRequest): Result<GuestBookResponse, DataError>

    suspend fun updateGuestBook(
        guestBookId: Long,
        request: UpdateGuestBookRequest
    ): Result<GuestBookResponse, DataError>

    suspend fun deleteGuestBook(guestBookId: Long): Result<Long, DataError>
}
