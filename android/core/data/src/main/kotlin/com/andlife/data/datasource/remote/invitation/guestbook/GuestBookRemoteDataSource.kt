package com.andlife.data.datasource.remote.invitation.guestbook

import com.andlife.domain.error.DataError
import com.andlife.domain.model.GuestBookMedia
import com.andlife.domain.util.Result
import com.andlife.network.api.guestbook.GuestBookResponse

interface GuestBookRemoteDataSource {
    suspend fun getMediaCollection(invitationId: Long): Result<List<GuestBookMedia>, DataError>

    suspend fun getGuestBooksByInvitationId(invitationId: Long): Result<List<GuestBookResponse>, DataError>
}
