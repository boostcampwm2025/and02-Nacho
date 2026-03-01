package com.andlife.data.datasource.remote.guestbook

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.andlife.data.repository.guestbook.toEntity
import com.andlife.database.InvitationDatabase
import com.andlife.database.entity.GuestBookEntity
import com.andlife.domain.util.Result

@OptIn(ExperimentalPagingApi::class)
class AllGuestBookRemoteMediator(
    private val remoteDataSource: GuestBookRemoteDataSource,
    private val database: InvitationDatabase,
) : RemoteMediator<Int, GuestBookEntity>() {

    private val dao = database.guestBookDao()

    override suspend fun initialize(): InitializeAction {
        return if (dao.count() > 0)
            InitializeAction.SKIP_INITIAL_REFRESH
        else
            InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, GuestBookEntity>
    ): MediatorResult {
        return try {

            val page = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val loadedItemCount = state.pages.sumOf { it.data.size }
                    if (loadedItemCount == 0) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    loadedItemCount / state.config.pageSize
                }
            }

            val result = remoteDataSource.getAllRelatedGuestBooks(
                page = page,
                size = state.config.pageSize
            )

            when (result) {
                is Result.Success -> {
                    val response = result.data

                    database.withTransaction {
                        if (loadType == LoadType.REFRESH) {
                            dao.clearAll()
                        }
                        val entities = response.content.map { it.toEntity() }

                        dao.upsertAll(entities)
                    }
                    MediatorResult.Success(
                        endOfPaginationReached = response.meta.isEnd || response.content.isEmpty()
                    )
                }

                is Result.Error -> {
                    MediatorResult.Error(Exception("${result.error}: ${result.message}"))
                }
            }
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}
