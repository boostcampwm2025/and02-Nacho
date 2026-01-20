package com.andlife.data.datasource.remote.invitation

import com.andlife.domain.error.DataError
import com.andlife.domain.util.Result
import com.andlife.network.api.invitation.InvitationResponse

interface InvitationRemoteDataSource {
    suspend fun getInvitation(invitationId: Long): Result<InvitationResponse, DataError>
    // Todo : 임시로 아이디만 받기 Invitation으로 수정 예정
    suspend fun getParticipantInvitations(): Result<List<Long>, DataError>
}
