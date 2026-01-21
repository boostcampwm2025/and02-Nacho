package com.andlife.domain.repository.invitation

import com.andlife.domain.error.DataError
import com.andlife.domain.model.invitation.CreateInvitationParam
import com.andlife.domain.model.invitation.Invitation
import com.andlife.domain.model.invitation.UpcomingInvitation
import com.andlife.domain.util.Result

interface InvitationRepository {
    suspend fun createInvitation(params: CreateInvitationParam): Result<Long, DataError>
    suspend fun getInvitation(invitationId: Long): Result<Invitation, DataError>
    suspend fun getUpcomingInvitations(days: Long = 30): Result<List<UpcomingInvitation>, DataError>
}

