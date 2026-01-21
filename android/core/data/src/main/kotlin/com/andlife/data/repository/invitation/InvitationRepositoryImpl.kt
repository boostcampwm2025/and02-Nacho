package com.andlife.data.repository.invitation

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.andlife.data.datasource.remote.invitation.InvitationPagingSource
import com.andlife.data.datasource.remote.invitation.InvitationRemoteDataSource
import com.andlife.domain.error.DataError
import com.andlife.domain.model.invitation.Invitation
import com.andlife.domain.model.invitation.InvitationSummary
import com.andlife.domain.repository.invitation.InvitationRepository
import com.andlife.domain.util.Result
import com.andlife.domain.util.map
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class InvitationRepositoryImpl @Inject constructor(
    private val invitationRemoteDataSource: InvitationRemoteDataSource,
) : InvitationRepository {
    override suspend fun getInvitation(invitationId: Long): Result<Invitation, DataError> =
        invitationRemoteDataSource.getInvitation(invitationId).map { response ->
            response.toDomain()
        }

    override fun getParticipantInvitations(
        status: String,
        size: Int
    ): Flow<PagingData<InvitationSummary>> {
        return Pager(
            config = PagingConfig(pageSize = size, enablePlaceholders = false),
            pagingSourceFactory = { InvitationPagingSource(invitationRemoteDataSource, status) }
        ).flow
    }
}
