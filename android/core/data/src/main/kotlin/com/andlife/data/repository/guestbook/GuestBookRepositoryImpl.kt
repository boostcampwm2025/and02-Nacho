package com.andlife.data.repository.guestbook

import com.andlife.data.datasource.remote.invitation.guestbook.GuestBookRemoteDataSource
import com.andlife.domain.error.DataError
import com.andlife.domain.model.guestbook.GalleryMedia
import com.andlife.domain.model.guestbook.GuestBook
import com.andlife.domain.repository.guestbook.GuestBookRepository
import com.andlife.domain.util.Result
import com.andlife.domain.util.map
import javax.inject.Inject

internal class GuestBookRepositoryImpl @Inject constructor(
    private val guestBookRemoteDataSource: GuestBookRemoteDataSource,
) : GuestBookRepository {
    override suspend fun getMediaCollection(invitationId: Long): Result<List<GalleryMedia>, DataError> {
        val result = guestBookRemoteDataSource.getMediaCollection(invitationId)

        return result.map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getGuestBooksByInvitationId(invitationId: Long): Result<List<GuestBook>, DataError> =
        guestBookRemoteDataSource.getGuestBooksByInvitationId(invitationId).map { list ->
            list.map { it.toDomain() }
        }
}
