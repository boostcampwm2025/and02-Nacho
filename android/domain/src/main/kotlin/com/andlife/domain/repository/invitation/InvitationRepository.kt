package com.andlife.domain.repository.invitation

import androidx.paging.PagingData
import com.andlife.domain.error.DataError
import com.andlife.domain.model.invitation.CreateInvitationParam
import com.andlife.domain.model.invitation.Invitation
import com.andlife.domain.model.invitation.InvitationSummary
import com.andlife.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface InvitationRepository {
    suspend fun createInvitation(params: CreateInvitationParam): Result<Long, DataError>
    suspend fun getInvitation(invitationId: Long): Result<Invitation, DataError>
    fun getParticipantInvitations(
        status: String,
        size: Int = 10
    ): Flow<PagingData<InvitationSummary>>
}

