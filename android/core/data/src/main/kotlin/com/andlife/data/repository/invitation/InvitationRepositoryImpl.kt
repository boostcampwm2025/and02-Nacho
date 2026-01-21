package com.andlife.data.repository.invitation

import com.andlife.data.datasource.remote.invitation.InvitationRemoteDataSource
import com.andlife.data.repository.invitation.mapper.toDomain
import com.andlife.data.repository.invitation.mapper.toRequest
import com.andlife.domain.error.DataError
import com.andlife.domain.model.invitation.CreateInvitationParam
import com.andlife.domain.model.invitation.Invitation
import com.andlife.domain.repository.invitation.InvitationRepository
import com.andlife.domain.util.Result
import com.andlife.domain.util.map
import kotlinx.serialization.json.Json
import javax.inject.Inject

internal class InvitationRepositoryImpl @Inject constructor(
    private val invitationRemoteDataSource: InvitationRemoteDataSource,
    private val json: Json
) : InvitationRepository {
    override suspend fun createInvitation(params: CreateInvitationParam): Result<Long, DataError> {
        val request = params.toRequest(json)
        return invitationRemoteDataSource.createInvitation(request).map { response ->
            response.id
        }
    }

    override suspend fun getInvitation(invitationId: Long): Result<Invitation, DataError> =
        invitationRemoteDataSource.getInvitation(invitationId).map { response ->
            response.toDomain(json)
        }
}
