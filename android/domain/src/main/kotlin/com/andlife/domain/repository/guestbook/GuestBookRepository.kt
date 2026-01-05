package com.andlife.domain.repository.guestbook

import com.andlife.domain.error.DataError
import com.andlife.domain.model.GuestBookMedia
import com.andlife.domain.util.Result

interface GuestBookRepository {
    suspend fun getMediaCollection(invitationId: Long): Result<List<GuestBookMedia>, DataError>
}
