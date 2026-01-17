package com.andlife.data.datasource.remote.invitation.guestbook

import com.andlife.domain.error.DataError
import com.andlife.domain.util.Result
import com.andlife.network.api.guestbook.GuestBookRequest
import com.andlife.network.api.guestbook.GuestBookResponse
import com.andlife.network.model.invitation.guestbook.CollectionResponse

interface GuestBookRemoteDataSource {
    suspend fun getMediaCollection(invitationId: Long): Result<List<CollectionResponse>, DataError>

    suspend fun getGuestBooksByInvitationId(invitationId: Long): Result<List<GuestBookResponse>, DataError>

    suspend fun createGuestBook(invitationId: Long, request: GuestBookRequest): Result<GuestBookResponse, DataError>
}
