package com.andlife.data.repository.thankscard

import com.andlife.data.datasource.remote.thankscard.ThanksCardRemoteDataSource
import com.andlife.data.repository.thankscard.mapper.toDomain
import com.andlife.data.repository.thankscard.mapper.toThanksCardRequest
import com.andlife.domain.error.DataError
import com.andlife.domain.model.card.NachoCard
import com.andlife.domain.repository.thankscard.ThanksCardRepository
import com.andlife.domain.util.Result
import com.andlife.domain.util.map
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ThanksCardRepositoryImpl @Inject constructor(
    private val thanksCardRemoteDataSource: ThanksCardRemoteDataSource,
    private val json: Json,
) : ThanksCardRepository {
    override suspend fun createThanksCard(
        invitationId: Long,
        card: NachoCard
    ): Result<Long, DataError> {
        val request = card.toThanksCardRequest(json)
        return thanksCardRemoteDataSource.createThanksCard(invitationId, request)
    }

    override suspend fun getThanksCard(invitationId: Long): Result<NachoCard, DataError> {
        val response = thanksCardRemoteDataSource.getThanksCard(invitationId)
        return response.map { it.toDomain(json) }
    }

    override suspend fun deleteThanksCard(invitationId: Long): Result<Unit, DataError> {
        return thanksCardRemoteDataSource.deleteThanksCard(invitationId).map { Unit }
    }

    override suspend fun updateThanksCard(
        cardId: Long,
        card: NachoCard
    ): Result<Long, DataError> {
        val request = card.toThanksCardRequest(json)
        return thanksCardRemoteDataSource.updateThanksCard(cardId, request).map { it.id }
    }
}
