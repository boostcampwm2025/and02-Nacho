package com.andlife.data.datasource.remote.invitation

import com.andlife.domain.error.DataError
import com.andlife.domain.model.invitation.InvitationStatus
import com.andlife.domain.model.invitation.SortDirection
import com.andlife.domain.util.Result
import com.andlife.network.api.invitation.CreateInvitationRequest
import com.andlife.network.api.invitation.InvitationCardRequest
import com.andlife.network.api.invitation.InvitationResponse
import com.andlife.network.model.PagingResponse
import com.andlife.network.model.invitation.InvitationSummaryResponse
import com.andlife.network.api.invitation.UpcomingInvitationResponse

interface InvitationRemoteDataSource {
    suspend fun createInvitation(request: CreateInvitationRequest): Result<InvitationResponse, DataError>
    suspend fun getInvitation(invitationId: Long): Result<InvitationResponse, DataError>
    suspend fun getParticipantInvitations(
        status: InvitationStatus,
        sortType: SortDirection,
        page: Int,
        size: Int
    ): Result<PagingResponse<InvitationSummaryResponse>, DataError>
    suspend fun getMyInvitations(
        status: InvitationStatus,
        sortType: SortDirection,
        page: Int,
        size: Int
    ): Result<PagingResponse<InvitationSummaryResponse>, DataError>
    suspend fun getUpcomingInvitations(
        days: Long,
        page: Int,
        size: Int,
    ): Result<PagingResponse<UpcomingInvitationResponse>, DataError>
    suspend fun createInvitationCard(invitationId: Long, request: InvitationCardRequest): Result<Long, DataError>
    suspend fun updateInvitationCard(cardId: Long, request: InvitationCardRequest): Result<Long, DataError>
}
