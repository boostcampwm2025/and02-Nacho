package com.andlife.data.datasource.remote.invitation.guestbook

import com.andlife.domain.error.DataError
import com.andlife.domain.model.GuestBook
import com.andlife.domain.model.GuestBookMedia
import com.andlife.domain.util.Result

interface GuestBookRemoteDataSource {
    suspend fun getMediaCollection(invitationId: Long): Result<List<GuestBookMedia>, DataError>

    suspend fun getGuestBooksByInvitationId(invitationId: Long): Result<List<GuestBook>, DataError>
}
