package com.andlife.data.repository.invitation

import com.andlife.data.datasource.remote.invitation.InvitationRemoteDataSource
import com.andlife.domain.error.DataError
import com.andlife.domain.model.invitation.Invitation
import com.andlife.domain.model.invitation.UpcomingInvitation
import com.andlife.domain.repository.invitation.InvitationRepository
import com.andlife.domain.util.Result
import com.andlife.domain.util.map
import javax.inject.Inject

internal class InvitationRepositoryImpl @Inject constructor(
    private val invitationRemoteDataSource: InvitationRemoteDataSource,
) : InvitationRepository {
    override suspend fun getInvitation(invitationId: Long): Result<Invitation, DataError> =
        invitationRemoteDataSource.getInvitation(invitationId).map { response ->
            response.toDomain()
        }

    override suspend fun getUpcomingInvitations(days: Long): Result<List<UpcomingInvitation>, DataError> =
        invitationRemoteDataSource.getUpcomingInvitations(days).map { response ->
            response.map { it.toDomain() }
        }
}
