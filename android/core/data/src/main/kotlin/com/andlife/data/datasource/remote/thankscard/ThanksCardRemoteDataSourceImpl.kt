package com.andlife.data.datasource.remote.thankscard

import com.andlife.data.util.apiCall
import com.andlife.domain.error.DataError
import com.andlife.domain.util.Result
import com.andlife.network.api.thankscard.ThanksCardService
import com.andlife.network.model.thankscard.ThanksCardRequest
import com.andlife.network.model.thankscard.ThanksCardResponse
import javax.inject.Inject

class ThanksCardRemoteDataSourceImpl @Inject constructor(
    private val thanksCardService: ThanksCardService
) : ThanksCardRemoteDataSource {
    override suspend fun createThanksCard(
        invitationId: Long,
        request: ThanksCardRequest
    ): Result<Long, DataError> = apiCall { thanksCardService.createThanksCard(invitationId, request) }

    override suspend fun getThanksCard(invitationId: Long): Result<ThanksCardResponse, DataError> {
        TODO("Not yet implemented")
    }
}
