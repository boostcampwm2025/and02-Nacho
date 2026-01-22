package com.andlife.data.datasource.remote.invitation

import com.andlife.domain.error.DataError
import com.andlife.domain.util.Result
import com.andlife.network.api.invitation.CreateInvitationRequest
import com.andlife.network.api.invitation.InvitationCardRequest
import com.andlife.network.api.invitation.InvitationResponse
import com.andlife.network.api.invitation.UpcomingInvitationResponse
import com.andlife.network.model.PagingResponse

interface InvitationRemoteDataSource {
    suspend fun createInvitation(request: CreateInvitationRequest): Result<InvitationResponse, DataError>
    suspend fun getInvitation(invitationId: Long): Result<InvitationResponse, DataError>
    // Todo : 임시로 아이디만 받기 Invitation으로 수정 예정
    suspend fun getParticipantInvitations(): Result<List<Long>, DataError>
    suspend fun getUpcomingInvitations(
        days: Long,
        page: Int,
        size: Int,
    ): Result<PagingResponse<UpcomingInvitationResponse>, DataError>
    suspend fun createInvitationCard(invitationId: Long, request: InvitationCardRequest): Result<Long, DataError>
    suspend fun updateInvitationCard(cardId: Long, request: InvitationCardRequest): Result<Long, DataError>
}
