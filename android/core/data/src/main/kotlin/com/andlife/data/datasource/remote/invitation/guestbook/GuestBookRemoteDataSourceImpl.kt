package com.andlife.data.datasource.remote.invitation.guestbook

import com.andlife.data.util.apiCall
import com.andlife.domain.error.DataError
import com.andlife.domain.util.Result
import com.andlife.network.api.guestbook.GuestBookRequest
import com.andlife.network.api.guestbook.GuestBookResponse
import com.andlife.network.api.guestbook.GuestBookService
import com.andlife.network.model.PagingResponse
import com.andlife.network.model.invitation.guestbook.CollectionResponse
import javax.inject.Inject

internal class GuestBookRemoteDataSourceImpl @Inject constructor(
    private val guestBookService: GuestBookService,
) : GuestBookRemoteDataSource {
    override suspend fun getMediaCollection(invitationId: Long): Result<List<CollectionResponse>, DataError> =
        apiCall { guestBookService.getMediaCollection(invitationId) }

    override suspend fun getGuestBooksByInvitationId(
        invitationId: Long,
        page: Int,
        size: Int,
    ): Result<PagingResponse<GuestBookResponse>, DataError> =
        apiCall { guestBookService.getGuestBooksByInvitationId(invitationId, page, size) }

    override suspend fun createGuestBook(
        invitationId: Long,
        request: GuestBookRequest
    ): Result<GuestBookResponse, DataError> =
        apiCall { guestBookService.createGuestBook(invitationId, request) }
}
