package com.andlife.data.datasource.remote.invitation

import com.andlife.domain.error.DataError
import com.andlife.domain.util.Result
import com.andlife.network.api.invitation.CreateInvitationRequest
import com.andlife.network.api.invitation.InvitationCardRequest
import com.andlife.network.api.invitation.InvitationResponse

interface InvitationRemoteDataSource {
    suspend fun createInvitation(request: CreateInvitationRequest): Result<InvitationResponse, DataError>
    suspend fun getInvitation(invitationId: Long): Result<InvitationResponse, DataError>
    suspend fun createInvitationCard(invitationId: Long, request: InvitationCardRequest): Result<Long, DataError>
    suspend fun updateInvitationCard(cardId: Long, request: InvitationCardRequest): Result<Long, DataError>
}
