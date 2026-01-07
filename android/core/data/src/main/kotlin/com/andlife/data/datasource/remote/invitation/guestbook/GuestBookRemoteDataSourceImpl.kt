package com.andlife.data.datasource.remote.invitation.guestbook

import com.andlife.data.util.apiCall
import com.andlife.domain.error.DataError
import com.andlife.domain.util.Result
import com.andlife.network.api.guestbook.GuestBookResponse
import com.andlife.network.api.guestbook.GuestBookService
import com.andlife.network.model.invitation.guestbook.CollectionResponse
import javax.inject.Inject

internal class GuestBookRemoteDataSourceImpl @Inject constructor(
    private val guestBookService: GuestBookService,
) : GuestBookRemoteDataSource {
    override suspend fun getMediaCollection(invitationId: Long): Result<List<CollectionResponse>, DataError> =
        apiCall { guestBookService.getMediaCollection(invitationId) }

    override suspend fun getGuestBooksByInvitationId(invitationId: Long): Result<List<GuestBookResponse>, DataError> =
        apiCall { guestBookService.getGuestBooksByInvitationId(invitationId) }
}
