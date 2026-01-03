package com.andlife.data.datasource.remote.address

import com.andlife.domain.error.DataError
import com.andlife.network.api.kakao.address.KakaoAddressResponse
import com.andlife.domain.util.Result

interface AddressRemoteDataSource {
    suspend fun searchAddress(
        query: String,
        page: Int,
        size: Int,
    ): Result<KakaoAddressResponse, DataError>
}
