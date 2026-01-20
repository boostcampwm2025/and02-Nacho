package com.andlife.data.repository.guestbook

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.andlife.data.datasource.remote.invitation.guestbook.GuestBookPagingSource
import com.andlife.data.datasource.remote.invitation.guestbook.GuestBookRemoteDataSource
import com.andlife.domain.error.DataError
import com.andlife.domain.model.guestbook.GalleryMedia
import com.andlife.domain.model.guestbook.GuestBook
import com.andlife.domain.model.guestbook.GuestBookMedia
import com.andlife.domain.repository.guestbook.GuestBookRepository
import com.andlife.domain.util.Result
import com.andlife.domain.util.map
import com.andlife.network.api.guestbook.GuestBookRequest
import kotlinx.coroutines.flow.Flow
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

    override fun getGuestBooksByInvitationId(invitationId: Long): Flow<PagingData<GuestBook>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                initialLoadSize = PAGE_SIZE,
            ),
            pagingSourceFactory = {
                GuestBookPagingSource(
                    remoteDataSource = guestBookRemoteDataSource,
                    invitationId = invitationId,
                )
            }
        ).flow

    override suspend fun createGuestBook(
        invitationId: Long,
        userId: Long,
        textContent: String,
        medias: List<GuestBookMedia>,
    ): Result<GuestBook, DataError> {
        val request =
            GuestBookRequest(
                userId = userId,
                textContent = textContent,
                medias = medias.map { it.toRequest() },
            )
        val result = guestBookRemoteDataSource.createGuestBook(invitationId, request)
        return result.map { it.toDomain() }
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}
