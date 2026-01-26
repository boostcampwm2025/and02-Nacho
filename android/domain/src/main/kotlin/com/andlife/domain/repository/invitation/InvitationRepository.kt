package com.andlife.domain.repository.invitation

import androidx.paging.PagingData
import com.andlife.domain.error.DataError
import com.andlife.domain.model.card.NachoCard
import com.andlife.domain.model.invitation.CreateInvitationParam
import com.andlife.domain.model.invitation.Invitation
import com.andlife.domain.model.invitation.InvitationJoin
import com.andlife.domain.model.invitation.InvitationStatus
import com.andlife.domain.model.invitation.InvitationSummary
import com.andlife.domain.model.invitation.SortDirection
import com.andlife.domain.model.invitation.UpcomingInvitation
import com.andlife.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface InvitationRepository {
    suspend fun joinInvitation(invitationId: Long): Result<InvitationJoin, DataError>
    suspend fun createInvitation(params: CreateInvitationParam): Result<Long, DataError>
    suspend fun getInvitation(invitationId: Long): Result<Invitation, DataError>
    fun getParticipantInvitations(
        status: InvitationStatus,
        sortType: SortDirection,
        isMyInvitation: Boolean
    ): Flow<PagingData<InvitationSummary>>
    fun getMyInvitations(
        status: InvitationStatus,
        sortType: SortDirection,
        isMyInvitation: Boolean
    ): Flow<PagingData<InvitationSummary>>
    fun getUpcomingInvitations(): Flow<PagingData<UpcomingInvitation>>
    suspend fun createInvitationCard(invitationId: Long, card: NachoCard): Result<Long, DataError>
    suspend fun updateCard(cardId: Long, card: NachoCard): Result<Long, DataError>
}

