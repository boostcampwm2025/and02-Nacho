package com.andlife.data.datasource.remote.invitation.guestbook

import com.andlife.domain.error.DataError
import com.andlife.domain.util.Result
import com.andlife.network.model.invitation.guestbook.CollectionResponse

interface GuestBookRemoteDataSource {
    suspend fun getMediaCollection(invitationId: Long): Result<List<CollectionResponse>, DataError>
}
