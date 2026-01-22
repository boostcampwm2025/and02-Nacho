package com.andlife.data.datasource.remote.invitation

import com.andlife.data.util.apiCall
import com.andlife.domain.error.DataError
import com.andlife.domain.util.Result
import com.andlife.network.api.invitation.CreateInvitationRequest
import com.andlife.network.api.invitation.InvitationCardRequest
import com.andlife.network.api.invitation.InvitationResponse
import com.andlife.network.api.invitation.InvitationService
import com.andlife.network.api.invitation.UpcomingInvitationResponse
import com.andlife.network.model.PagingResponse
import javax.inject.Inject

internal class InvitationRemoteDataSourceImpl @Inject constructor(
    private val invitationService: InvitationService,
) : InvitationRemoteDataSource {
    override suspend fun createInvitation(request: CreateInvitationRequest): Result<InvitationResponse, DataError> =
        apiCall { invitationService.createInvitation(request) }

    override suspend fun getInvitation(invitationId: Long): Result<InvitationResponse, DataError> =
        apiCall { invitationService.getInvitation(invitationId) }

    override suspend fun getParticipantInvitations(): Result<List<Long>, DataError> =
        apiCall { invitationService.getParticipantInvitations() }

    override suspend fun getUpcomingInvitations(
        days: Long,
        page: Int,
        size: Int,
    ): Result<PagingResponse<UpcomingInvitationResponse>, DataError> =
        apiCall { invitationService.getUpcomingInvitations(days, page, size) }

    override suspend fun createInvitationCard(
        invitationId: Long,
        request: InvitationCardRequest
    ): Result<Long, DataError> =
        apiCall { invitationService.createInvitationCard(invitationId, request) }

    override suspend fun updateInvitationCard(
        cardId: Long,
        request: InvitationCardRequest
    ): Result<Long, DataError> =
        apiCall { invitationService.updateInvitationCard(cardId, request) }
}

