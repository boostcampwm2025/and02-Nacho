package com.andlife.data.repository.guestbook

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.andlife.data.datasource.remote.guestbook.HomeGuestBookRemoteMediator
import com.andlife.data.datasource.remote.guestbook.GuestBookRemoteDataSource
import com.andlife.data.datasource.remote.guestbook.GuestBookRemoteMediator
import com.andlife.database.dao.GuestBookDao
import com.andlife.database.dao.HomeGuestBookDao
import com.andlife.domain.error.DataError
import com.andlife.domain.model.guestbook.GalleryMedia
import com.andlife.domain.model.guestbook.GuestBook
import com.andlife.domain.model.guestbook.GuestBookMedia
import com.andlife.domain.repository.guestbook.GuestBookRepository
import com.andlife.domain.util.Result
import com.andlife.domain.util.map
import com.andlife.domain.util.onSuccess
import com.andlife.network.model.guestbook.GuestBookRequest
import com.andlife.network.model.guestbook.UpdateGuestBookRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class GuestBookRepositoryImpl @Inject constructor(
    private val guestBookRemoteDataSource: GuestBookRemoteDataSource,
    private val guestBookDao: GuestBookDao,
    private val homeGuestBookDao: HomeGuestBookDao,
    private val homeGuestBookRemoteMediator: HomeGuestBookRemoteMediator,
    private val guestBookRemoteMediatorFactory: GuestBookRemoteMediator.Factory,
) : GuestBookRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getAllRelatedGuestBooks(): Flow<PagingData<GuestBook>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                initialLoadSize = PAGE_SIZE
            ),
            remoteMediator = homeGuestBookRemoteMediator,
            pagingSourceFactory = { homeGuestBookDao.pagingSource() }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }

    @OptIn(ExperimentalPagingApi::class)
    override fun getGuestBooksByInvitationId(invitationId: Long): Flow<PagingData<GuestBook>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = true,
                initialLoadSize = PAGE_SIZE,
            ),
            remoteMediator = guestBookRemoteMediatorFactory.create(invitationId),
            pagingSourceFactory = { guestBookDao.pagingSourceByInvitationId(invitationId) }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }

    override suspend fun getMediaCollection(invitationId: Long): Result<List<GalleryMedia>, DataError> =
        guestBookRemoteDataSource.getMediaCollection(invitationId).map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun createGuestBook(
        invitationId: Long,
        textContent: String,
        medias: List<GuestBookMedia>,
    ): Result<GuestBook, DataError> {
        val request = GuestBookRequest(
            textContent = textContent,
            medias = medias.map { it.toRequest() },
        )
        return guestBookRemoteDataSource.createGuestBook(invitationId, request)
            .onSuccess { response ->
                guestBookDao.upsertAll(listOf(response.toEntity()))
                homeGuestBookDao.upsertAll(listOf(response.toHomeEntity()))
            }
            .map { it.toDomain() }
    }

    override suspend fun updateGuestBook(
        guestBookId: Long,
        textContent: String,
        existingImageIds: List<Long>,
        existingVideoIds: List<Long>,
        existingAudioIds: List<Long>,
        newMedias: List<GuestBookMedia>
    ): Result<GuestBook, DataError> {
        val request = UpdateGuestBookRequest(
            textContent = textContent,
            existingImageIds = existingImageIds,
            existingVideoIds = existingVideoIds,
            existingAudioIds = existingAudioIds,
            newMedias = newMedias.map { it.toRequest() },
        )
        return guestBookRemoteDataSource.updateGuestBook(guestBookId, request)
            .onSuccess { response ->
                guestBookDao.upsertAll(listOf(response.toEntity()))
                homeGuestBookDao.upsertAll(listOf(response.toHomeEntity()))
            }
            .map { it.toDomain() }
    }

    override suspend fun deleteGuestBook(guestBookId: Long): Result<Long, DataError> =
        guestBookRemoteDataSource.deleteGuestBook(guestBookId).onSuccess {
            guestBookDao.deleteById(guestBookId)
            homeGuestBookDao.deleteById(guestBookId)
        }

    companion object {
        private const val PAGE_SIZE = 10
    }
}
