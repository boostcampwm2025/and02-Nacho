package com.andlife.data.datasource.remote.invitation

import com.andlife.domain.error.DataError
import com.andlife.domain.util.Result
import com.andlife.network.api.invitation.CreateInvitationRequest
import com.andlife.network.api.invitation.InvitationResponse
import com.andlife.network.api.invitation.UpcomingInvitationResponse

interface InvitationRemoteDataSource {
    suspend fun createInvitation(request: CreateInvitationRequest): Result<InvitationResponse, DataError>
    suspend fun getInvitation(invitationId: Long): Result<InvitationResponse, DataError>
    suspend fun getUpcomingInvitations(days: Long): Result<List<UpcomingInvitationResponse>, DataError>
}
