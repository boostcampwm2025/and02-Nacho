package com.andlife.domain.repository.thankscard

import com.andlife.domain.error.DataError
import com.andlife.domain.model.card.NachoCard
import com.andlife.domain.util.Result

interface ThanksCardRepository {
    suspend fun createThanksCard(invitationId: Long, card: NachoCard): Result<Long, DataError>
    suspend fun getThanksCard(invitationId: Long): Result<NachoCard, DataError>
    suspend fun deleteThanksCard(invitationId: Long): Result<Unit, DataError>
    suspend fun updateThanksCard(cardId: Long, card: NachoCard): Result<Long, DataError>
}
