package com.andlife.data.datasource.remote.invitation

import com.andlife.domain.error.DataError
import com.andlife.domain.util.Result
import com.andlife.network.api.invitation.InvitationResponse
import com.andlife.network.model.PagingResponse
import com.andlife.network.model.invitation.InvitationSummaryResponse

interface InvitationRemoteDataSource {
    suspend fun getInvitation(invitationId: Long): Result<InvitationResponse, DataError>
    suspend fun getParticipantInvitations(
        status: String,
        page: Int,
        size: Int
    ): Result<PagingResponse<InvitationSummaryResponse>, DataError>
}
