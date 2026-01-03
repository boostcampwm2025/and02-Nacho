package com.andlife.data.datasource.remote.address

import com.andlife.domain.error.DataError
import com.andlife.domain.util.Result
import com.andlife.network.api.kakao.address.KakaoAddressResponse

interface AddressRemoteDataSource {
    suspend fun searchAddress(
        query: String,
        page: Int,
        size: Int,
    ): Result<KakaoAddressResponse, DataError>
}
