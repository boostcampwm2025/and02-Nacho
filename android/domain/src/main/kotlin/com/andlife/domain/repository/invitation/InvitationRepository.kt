package com.andlife.domain.repository.invitation

import androidx.paging.PagingData
import com.andlife.domain.error.DataError
import com.andlife.domain.model.card.NachoCard
import com.andlife.domain.model.invitation.CreateInvitationParam
import com.andlife.domain.model.invitation.Invitation
import com.andlife.domain.model.invitation.UpcomingInvitation
import com.andlife.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface InvitationRepository {
    suspend fun createInvitation(params: CreateInvitationParam): Result<Long, DataError>
    suspend fun getInvitation(invitationId: Long): Result<Invitation, DataError>
    // Todo: 임시로 아이디만 받기 Invitation으로 수정 예정
    suspend fun getParticipantInvitations(): Result<List<Long>, DataError>
    fun getUpcomingInvitations(): Flow<PagingData<UpcomingInvitation>>
    suspend fun createInvitationCard(invitationId: Long, card: NachoCard): Result<Long, DataError>
    suspend fun updateCard(cardId: Long, card: NachoCard): Result<Long, DataError>
}

