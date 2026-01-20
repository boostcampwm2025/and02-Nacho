package com.andlife.data.datasource.remote.invitation

import com.andlife.domain.error.DataError
import com.andlife.domain.util.Result
import com.andlife.network.api.invitation.InvitationResponse
import com.andlife.network.api.invitation.UpcomingInvitationResponse

interface InvitationRemoteDataSource {
    suspend fun getInvitation(invitationId: Long): Result<InvitationResponse, DataError>
    suspend fun getUpcomingInvitations(): Result<List<UpcomingInvitationResponse>, DataError>
}
