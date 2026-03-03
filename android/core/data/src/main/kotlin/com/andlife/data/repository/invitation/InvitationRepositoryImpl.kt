package com.andlife.data.repository.invitation

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.andlife.data.datasource.remote.invitation.InvitationRemoteDataSource
import com.andlife.data.datasource.remote.invitation.InvitationRemoteMediator
import com.andlife.data.datasource.remote.invitation.UpcomingInvitationRemoteMediator
import com.andlife.data.repository.invitation.mapper.toDomain
import com.andlife.data.repository.invitation.mapper.toRequest
import com.andlife.database.InvitationDatabase
import com.andlife.datastore.UserStorage
import com.andlife.domain.error.DataError
import com.andlife.domain.model.card.NachoCard
import com.andlife.domain.model.invitation.Invitation
import com.andlife.domain.model.invitation.InvitationJoin
import com.andlife.domain.model.invitation.InvitationSaveParam
import com.andlife.domain.model.invitation.InvitationStatus
import com.andlife.domain.model.invitation.InvitationSummary
import com.andlife.domain.model.invitation.SortDirection
import com.andlife.domain.model.invitation.UpcomingInvitation
import com.andlife.domain.repository.invitation.InvitationRepository
import com.andlife.domain.util.Result
import com.andlife.domain.util.map
import com.andlife.domain.util.onSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject

internal class InvitationRepositoryImpl @Inject constructor(
    private val invitationRemoteDataSource: InvitationRemoteDataSource,
    private val database: InvitationDatabase,
    private val userStorage: UserStorage,
    private val json: Json
) : InvitationRepository {
    override suspend fun joinInvitation(invitationId: Long): Result<InvitationJoin, DataError> {
        return invitationRemoteDataSource.joinInvitation(invitationId).map { dto ->
            dto.toDomain().also { domainModel ->
                if (!domainModel.isMember) {
                    userStorage.addInvitationId(domainModel.invitationId)
                    Log.d("InvitationRepositoryImpl", "참여 성공: ${domainModel.invitationId}")
                }
            }
        }
    }

    override suspend fun createInvitation(params: InvitationSaveParam): Result<Long, DataError> {
        val request = params.toRequest(json)
        return invitationRemoteDataSource.createInvitation(request).map { response ->
            response.id
        }
    }

    override suspend fun updateInvitation(
        invitationId: Long,
        params: InvitationSaveParam
    ): Result<Long, DataError> {
        val request = params.toRequest(json)
        return invitationRemoteDataSource.updateInvitation(invitationId, request).map { response ->
            response.id
        }
    }

    override suspend fun leaveInvitation(invitationId: Long): Result<Unit, DataError> =
        invitationRemoteDataSource.leaveInvitation(invitationId).onSuccess {
            userStorage.deleteInvitationId(invitationId)
            database.invitationSummaryDao().deleteById(invitationId)
            database.upcomingInvitationDao().deleteById(invitationId)
        }

    override suspend fun deleteInvitation(invitationId: Long): Result<Unit, DataError> =
        invitationRemoteDataSource.deleteInvitation(invitationId).onSuccess {
            database.invitationSummaryDao().deleteById(invitationId)
            database.upcomingInvitationDao().deleteById(invitationId)
        }

    override suspend fun getInvitation(invitationId: Long): Result<Invitation, DataError> =
        invitationRemoteDataSource.getInvitation(invitationId).map { response ->
            response.toDomain(json)
        }

    @OptIn(ExperimentalPagingApi::class)
    override fun getParticipantInvitations(
        status: InvitationStatus,
        sortType: SortDirection,
        isMyInvitation: Boolean,
        onTotalCountLoaded: (Int) -> Unit
    ): Flow<PagingData<InvitationSummary>> {
        val dao = database.invitationSummaryDao()

        return Pager(
            config = createPagingConfig(),
            remoteMediator = InvitationRemoteMediator(
                remoteDataSource = invitationRemoteDataSource,
                database = database,
                status = status,
                sortType = sortType,
                isMyInvitation = isMyInvitation,
                onTotalCountLoaded = onTotalCountLoaded
            ),
            pagingSourceFactory = {
                if (sortType == SortDirection.ASC) {
                    dao.pagingSourceAsc(status.name, isMyInvitation)
                } else {
                    dao.pagingSourceDesc(status.name, isMyInvitation)
                }
            }
        ).flow.map { pagingData ->
            pagingData.map { entity -> entity.toDomain() }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getMyInvitations(
        status: InvitationStatus,
        sortType: SortDirection,
        isMyInvitation: Boolean,
        onTotalCountLoaded: (Int) -> Unit
    ): Flow<PagingData<InvitationSummary>> {
        val dao = database.invitationSummaryDao()

        return Pager(
            config = createPagingConfig(),
            remoteMediator = InvitationRemoteMediator(
                remoteDataSource = invitationRemoteDataSource,
                database = database,
                status = status,
                sortType = sortType,
                isMyInvitation = isMyInvitation,
                onTotalCountLoaded = onTotalCountLoaded
            ),
            pagingSourceFactory = {
                if (sortType == SortDirection.ASC) {
                    dao.pagingSourceAsc(status.name, isMyInvitation)
                } else {
                    dao.pagingSourceDesc(status.name, isMyInvitation)
                }
            }
        ).flow.map { pagingData ->
            pagingData.map { entity -> entity.toDomain() }
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getUpcomingInvitations(): Flow<PagingData<UpcomingInvitation>> =
        Pager(
            config = createPagingConfig(),
            remoteMediator = UpcomingInvitationRemoteMediator(
                remoteDataSource = invitationRemoteDataSource,
                database = database,
                days = UPCOMING_DAYS_THRESHOLD,
            ),
            pagingSourceFactory = {
                database.upcomingInvitationDao().pagingSource()
            }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }

    override suspend fun createInvitationCard(
        invitationId: Long,
        card: NachoCard
    ): Result<Long, DataError> {
        val cardRequest = card.toRequest(json)
        return invitationRemoteDataSource.createInvitationCard(invitationId, cardRequest)
    }

    override suspend fun updateCard(
        cardId: Long,
        card: NachoCard
    ): Result<Long, DataError> {
        val cardRequest = card.toRequest(json)
        return invitationRemoteDataSource.updateInvitationCard(cardId, cardRequest)
    }

    private fun createPagingConfig() = PagingConfig(
        pageSize = PAGE_SIZE,
        enablePlaceholders = false,
        initialLoadSize = PAGE_SIZE
    )

    companion object {
        private const val PAGE_SIZE = 10
        private const val UPCOMING_DAYS_THRESHOLD = 30L
    }
}
