package com.andlife.data.datasource.remote.invitation

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.andlife.data.repository.invitation.mapper.toEntity
import com.andlife.database.InvitationDatabase
import com.andlife.database.entity.UpcomingInvitationEntity
import com.andlife.domain.util.Result
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class UpcomingInvitationRemoteMediator @Inject constructor(
    private val remoteDataSource: InvitationRemoteDataSource,
    private val database: InvitationDatabase,
) : RemoteMediator<Int, UpcomingInvitationEntity>() {

    private val dao = database.upcomingInvitationDao()

    override suspend fun initialize(): InitializeAction = InitializeAction.LAUNCH_INITIAL_REFRESH

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, UpcomingInvitationEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastPage = state.pages.lastOrNull { it.data.isNotEmpty() }
                    if (lastPage == null) {
                        return MediatorResult.Success(endOfPaginationReached = false)
                    }
                    state.pages.size
                }
            }

            val result = remoteDataSource.getUpcomingInvitations(
                days = UPCOMING_DAYS_THRESHOLD,
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

    companion object {
        private const val UPCOMING_DAYS_THRESHOLD = 30L
    }
}
