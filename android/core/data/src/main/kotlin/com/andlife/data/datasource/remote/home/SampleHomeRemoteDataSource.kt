package com.andlife.data.datasource.remote.home

import com.andlife.domain.error.DataError
import com.andlife.domain.util.Result
import com.andlife.network.api.home.HomeResponse
import com.andlife.network.model.BaseResponse
import com.andlife.network.util.apiCall

interface SampleHomeRemoteDataSource {
    suspend fun getSample(): Result<HomeResponse, DataError>
}

class SampleHomeRemoteDataSourceImpl(

) : SampleHomeRemoteDataSource {
    override suspend fun getSample(): Result<HomeResponse, DataError> {
        return apiCall {
            // todo: apiService 호출하기
            BaseResponse(
                code = 200,
                data = HomeResponse("hihi"),
                message = null
            )
        }
    }
}