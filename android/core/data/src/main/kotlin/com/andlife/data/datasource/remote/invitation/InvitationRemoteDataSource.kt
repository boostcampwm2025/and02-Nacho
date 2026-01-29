package com.andlife.data.datasource.remote.invitation

import com.andlife.domain.error.DataError
import com.andlife.domain.model.invitation.InvitationStatus
import com.andlife.domain.model.invitation.SortDirection
import com.andlife.domain.util.Result
import com.andlife.network.model.invitation.InvitationSaveRequest
import com.andlife.network.model.invitation.InvitationCardRequest
import com.andlife.network.model.invitation.InvitationResponse
import com.andlife.network.model.PagingResponse
import com.andlife.network.model.invitation.InvitationSummaryResponse
import com.andlife.network.model.invitation.JoinResponse
import com.andlife.network.model.invitation.UpcomingInvitationResponse

interface InvitationRemoteDataSource {
    suspend fun joinInvitation(invitationId: Long): Result<JoinResponse, DataError>
    suspend fun leaveInvitation(invitationId: Long): Result<Unit, DataError>
    suspend fun createInvitation(request: InvitationSaveRequest): Result<InvitationResponse, DataError>
    suspend fun updateInvitation(invitationId: Long, request: InvitationSaveRequest): Result<InvitationResponse, DataError>
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
    suspend fun deleteInvitation(invitationId: Long): Result<Unit, DataError>
}
