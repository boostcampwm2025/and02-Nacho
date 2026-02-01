package com.andlife.data.datasource.remote.thankscard

import com.andlife.domain.error.DataError
import com.andlife.domain.util.Result
import com.andlife.network.model.thankscard.ThanksCardRequest
import com.andlife.network.model.thankscard.ThanksCardResponse

interface ThanksCardRemoteDataSource {
    suspend fun createThanksCard(invitationId: Long, request: ThanksCardRequest): Result<Long, DataError>
    suspend fun getThanksCard(invitationId: Long): Result<ThanksCardResponse, DataError>
}
