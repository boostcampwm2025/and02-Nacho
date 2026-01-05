package com.andlife.data.repository.guestbook

import com.andlife.data.datasource.remote.invitation.guestbook.GuestBookRemoteDataSource
import com.andlife.domain.error.DataError
import com.andlife.domain.model.GuestBook
import com.andlife.domain.model.GuestBookMedia
import com.andlife.domain.repository.guestbook.GuestBookRepository
import com.andlife.domain.util.Result
import javax.inject.Inject

class GuestBookRepositoryImpl
    @Inject
    constructor(
        private val guestBookRemoteDataSource: GuestBookRemoteDataSource,
    ) : GuestBookRepository {
        override suspend fun getMediaCollection(invitationId: Long): Result<List<GuestBookMedia>, DataError> =
            guestBookRemoteDataSource.getMediaCollection(invitationId)

        override suspend fun getGuestBooksByInvitationId(invitationId: Long): Result<List<GuestBook>, DataError> =
            guestBookRemoteDataSource.getGuestBooksByInvitationId(invitationId)
    }
