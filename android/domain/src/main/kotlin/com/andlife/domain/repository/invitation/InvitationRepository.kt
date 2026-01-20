package com.andlife.domain.repository.invitation

import com.andlife.domain.error.DataError
import com.andlife.domain.model.invitation.Invitation
import com.andlife.domain.model.invitation.UpcomingInvitation
import com.andlife.domain.util.Result

interface InvitationRepository {
    suspend fun getInvitation(invitationId: Long): Result<Invitation, DataError>
    suspend fun getUpcomingInvitations(): Result<List<UpcomingInvitation>, DataError>
}

