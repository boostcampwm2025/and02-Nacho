package com.andlife.data.repository.invitation

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.andlife.data.datasource.remote.invitation.InvitationPagingSource
import com.andlife.data.datasource.remote.invitation.InvitationRemoteDataSource
import com.andlife.data.datasource.remote.invitation.UpcomingInvitationPagingSource
import com.andlife.data.repository.invitation.mapper.toDomain
import com.andlife.data.repository.invitation.mapper.toRequest
import com.andlife.domain.error.DataError
import com.andlife.domain.model.card.NachoCard
import com.andlife.domain.model.invitation.InvitationSaveParam
import com.andlife.domain.model.invitation.Invitation
import com.andlife.domain.model.invitation.InvitationStatus
import com.andlife.domain.model.invitation.InvitationSummary
import com.andlife.domain.model.invitation.SortDirection
import com.andlife.domain.model.invitation.UpcomingInvitation
import com.andlife.domain.repository.invitation.InvitationRepository
import com.andlife.domain.util.Result
import com.andlife.domain.util.map
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import javax.inject.Inject

internal class InvitationRepositoryImpl @Inject constructor(
    private val invitationRemoteDataSource: InvitationRemoteDataSource,
    private val json: Json
) : InvitationRepository {
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

    override suspend fun getInvitation(invitationId: Long): Result<Invitation, DataError> =
        invitationRemoteDataSource.getInvitation(invitationId).map { response ->
            response.toDomain(json)
        }

    override fun getParticipantInvitations(
        status: InvitationStatus,
        sortType: SortDirection,
        isMyInvitation: Boolean
    ): Flow<PagingData<InvitationSummary>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                initialLoadSize = PAGE_SIZE
            ),
            pagingSourceFactory = { InvitationPagingSource(invitationRemoteDataSource, status, sortType, isMyInvitation) }
        ).flow
    }

    override fun getMyInvitations(
        status: InvitationStatus,
        sortType: SortDirection,
        isMyInvitation: Boolean
    ): Flow<PagingData<InvitationSummary>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                initialLoadSize = PAGE_SIZE
            ),
            pagingSourceFactory = { InvitationPagingSource(invitationRemoteDataSource, status, sortType, isMyInvitation) }
        ).flow
    }

    override fun getUpcomingInvitations(): Flow<PagingData<UpcomingInvitation>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                initialLoadSize = PAGE_SIZE,
            ),
            pagingSourceFactory = {
                UpcomingInvitationPagingSource(
                    remoteDataSource = invitationRemoteDataSource,
                    days = UPCOMING_DAYS_THRESHOLD,
                )
            }
        ).flow

    companion object {
        private const val PAGE_SIZE = 10
        private const val UPCOMING_DAYS_THRESHOLD = 30L
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
}
