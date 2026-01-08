package com.andlife.data.datasource.remote.invitation.guestbook

import com.andlife.domain.error.DataError
import com.andlife.domain.util.Result
import com.andlife.network.model.invitation.guestbook.CollectionResponse
import com.andlife.network.model.invitation.guestbook.CreateGuestBookRequest
import com.andlife.network.model.invitation.guestbook.GuestBookResponse

interface GuestBookRemoteDataSource {
    suspend fun createGuestBook(request: CreateGuestBookRequest): Result<GuestBookResponse, DataError>

    suspend fun getMediaCollection(invitationId: Long): Result<List<CollectionResponse>, DataError>
}
