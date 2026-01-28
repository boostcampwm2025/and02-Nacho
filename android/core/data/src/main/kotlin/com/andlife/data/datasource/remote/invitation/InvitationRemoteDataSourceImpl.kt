package com.andlife.data.datasource.remote.invitation

import com.andlife.data.util.apiCall
import com.andlife.domain.error.DataError
import com.andlife.domain.model.invitation.InvitationStatus
import com.andlife.domain.model.invitation.SortDirection
import com.andlife.domain.util.Result
import com.andlife.network.model.invitation.InvitationSaveRequest
import com.andlife.network.model.invitation.InvitationCardRequest
import com.andlife.network.model.invitation.InvitationResponse
import com.andlife.network.api.invitation.InvitationService
import com.andlife.network.model.PagingResponse
import com.andlife.network.model.invitation.InvitationSummaryResponse
import com.andlife.network.model.invitation.UpcomingInvitationResponse
import javax.inject.Inject

internal class InvitationRemoteDataSourceImpl @Inject constructor(
    private val invitationService: InvitationService,
) : InvitationRemoteDataSource {
    override suspend fun createInvitation(request: InvitationSaveRequest): Result<InvitationResponse, DataError> =
        apiCall { invitationService.createInvitation(request) }

    override suspend fun updateInvitation(
        invitationId: Long,
        request: InvitationSaveRequest
    ): Result<InvitationResponse, DataError> =
        apiCall { invitationService.updateInvitation(invitationId, request) }

    override suspend fun getInvitation(invitationId: Long): Result<InvitationResponse, DataError> =
        apiCall { invitationService.getInvitation(invitationId) }

    override suspend fun getParticipantInvitations(
        status: InvitationStatus,
        sortType: SortDirection,
        page: Int,
        size: Int
    ): Result<PagingResponse<InvitationSummaryResponse>, DataError> =
        apiCall { invitationService.getParticipantInvitations(status.value, sortType.value, page, size) }

    override suspend fun getMyInvitations(
        status: InvitationStatus,
        sortType: SortDirection,
        page: Int,
        size: Int
    ): Result<PagingResponse<InvitationSummaryResponse>, DataError> =
        apiCall { invitationService.getMyInvitations(status.value, sortType.value, page, size) }

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

