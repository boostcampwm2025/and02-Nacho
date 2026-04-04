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
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

@OptIn(ExperimentalPagingApi::class)
class InvitationRemoteMediator @AssistedInject constructor(
    @Assisted private val status: InvitationStatus,
    @Assisted private val sortType: SortDirection,
    @Assisted private val isMyInvitation: Boolean,
    @Assisted private val onTotalCountLoaded: (Int) -> Unit,
    private val remoteDataSource: InvitationRemoteDataSource,
    private val database: InvitationDatabase,
) : RemoteMediator<Int, InvitationSummaryEntity>() {

    @AssistedFactory
    interface Factory {
        fun create(
            status: InvitationStatus,
            sortType: SortDirection,
            isMyInvitation: Boolean,
            onTotalCountLoaded: (Int) -> Unit,
        ): InvitationRemoteMediator
    }

    private val dao = database.invitationSummaryDao()

    override suspend fun initialize(): InitializeAction = InitializeAction.LAUNCH_INITIAL_REFRESH

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, InvitationSummaryEntity>
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
