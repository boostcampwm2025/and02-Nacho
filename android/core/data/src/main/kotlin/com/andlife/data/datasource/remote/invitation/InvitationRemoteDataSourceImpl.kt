package com.andlife.data.datasource.remote.invitation

import com.andlife.data.util.apiCall
import com.andlife.domain.error.DataError
import com.andlife.domain.util.Result
import com.andlife.network.api.invitation.InvitationResponse
import com.andlife.network.api.invitation.InvitationService
import com.andlife.network.api.invitation.UpcomingInvitationResponse
import javax.inject.Inject

internal class InvitationRemoteDataSourceImpl @Inject constructor(
    private val invitationService: InvitationService,
) : InvitationRemoteDataSource {
    override suspend fun getInvitation(invitationId: Long): Result<InvitationResponse, DataError> =
        apiCall { invitationService.getInvitation(invitationId) }

    override suspend fun getUpcomingInvitations(): Result<List<UpcomingInvitationResponse>, DataError> =
        apiCall { invitationService.getUpcomingInvitations() }
}
