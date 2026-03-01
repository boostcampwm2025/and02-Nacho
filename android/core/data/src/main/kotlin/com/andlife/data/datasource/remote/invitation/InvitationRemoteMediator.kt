package com.andlife.data.datasource.remote.invitation

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.andlife.data.repository.invitation.mapper.toEntity
import com.andlife.database.InvitationDatabase
import com.andlife.database.entity.InvitationSummaryEntity
import com.andlife.domain.model.invitation.InvitationStatus
import com.andlife.domain.model.invitation.SortDirection
import com.andlife.domain.util.Result

@OptIn(ExperimentalPagingApi::class)
class InvitationRemoteMediator(
    private val remoteDataSource: InvitationRemoteDataSource,
    private val database: InvitationDatabase,
    private val status: InvitationStatus,
    private val sortType: SortDirection,
    private val isMyInvitation: Boolean,
    private val onTotalCountLoaded: (Int) -> Unit
) : RemoteMediator<Int, InvitationSummaryEntity>() {

    private val dao = database.invitationSummaryDao()

    override suspend fun initialize(): InitializeAction = InitializeAction.SKIP_INITIAL_REFRESH

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, InvitationSummaryEntity>
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

            val result = if (isMyInvitation) {
                remoteDataSource.getMyInvitations(
                    status = status,
                    sortType = sortType,
                    page = page,
                    size = state.config.pageSize
                )
            } else {
                remoteDataSource.getParticipantInvitations(
                    status = status,
                    sortType = sortType,
                    page = page,
                    size = state.config.pageSize
                )
            }

            when (result) {
                is Result.Success -> {
                    val response = result.data
                    onTotalCountLoaded(response.meta.totalCount)

                    database.withTransaction {
                        if (loadType == LoadType.REFRESH) {
                            dao.clearByQuery(status.name, isMyInvitation)
                        }

                        val entities = response.content.map { dto ->
                            dto.toEntity(
                                status = status.name,
                                isMyInvitation = isMyInvitation,
                            )
                        }
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
