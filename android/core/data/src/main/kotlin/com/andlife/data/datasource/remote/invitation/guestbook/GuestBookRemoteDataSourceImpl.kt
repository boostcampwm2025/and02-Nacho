package com.andlife.data.datasource.remote.invitation.guestbook

import com.andlife.data.util.apiCall
import com.andlife.domain.error.DataError
import com.andlife.domain.model.GuestBook
import com.andlife.domain.model.GuestBookMedia
import com.andlife.domain.model.MediaType
import com.andlife.domain.util.Result
import com.andlife.domain.util.map
import com.andlife.network.api.guestbook.GuestBookService
import com.andlife.network.di.Invitation
import com.andlife.network.model.invitation.guestbook.CollectionResponse
import javax.inject.Inject

class GuestBookRemoteDataSourceImpl @Inject constructor(
    @param:Invitation private val guestBookService: GuestBookService
) : GuestBookRemoteDataSource {

    override suspend fun getMediaCollection(invitationId: Long): Result<List<GuestBookMedia>, DataError> {
        val result: Result<List<CollectionResponse>, DataError> = apiCall {
            guestBookService.getMediaCollection(invitationId)
        }

        return result.map { networkList ->
            networkList.map { response ->
                GuestBookMedia(
                    id = response.id,
                    type = MediaType.fromString(response.mediaType),
                    url = response.mediaUrl,
                    content = response.content,
                    authorName = response.author.name,
                    authorProfileUrl = response.author.profileImageUrl,
                    createdAt = response.createdAt,
                    durationSeconds = response.durationSeconds
                )
            }
        }
    }

    override suspend fun getGuestBooksByInvitationId(invitationId: Long): Result<List<GuestBook>, DataError> = apiCall {
        guestBookService.getGuestBooksByInvitationId(invitationId)
    }.map { list -> list.map { it.toDomain() } }
}
