package com.andlife.domain.repository.invitation

import com.andlife.domain.error.DataError
import com.andlife.domain.model.invitation.Invitation
import com.andlife.domain.util.Result

interface InvitationRepository {
    suspend fun getInvitation(invitationId: Long): Result<Invitation, DataError>
    // Todo: 임시로 아이디만 받기 Invitation으로 수정 예정
    suspend fun getParticipantInvitations(): Result<List<Long>, DataError>
}

