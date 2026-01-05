package com.andlife.data.datasource.remote.address

import com.andlife.data.util.externalApiCall
import com.andlife.domain.error.DataError
import com.andlife.domain.util.Result
import com.andlife.network.api.kakao.address.KakaoAddressResponse
import com.andlife.network.api.kakao.address.KakaoAddressService
import javax.inject.Inject

internal class AddressRemoteDataSourceImpl @Inject constructor(
    private val kakaoAddressService: KakaoAddressService,
) : AddressRemoteDataSource {
    override suspend fun searchAddress(
        query: String,
        page: Int,
        size: Int,
    ): Result<KakaoAddressResponse, DataError> =
        externalApiCall {
            kakaoAddressService.searchAddress(
                query = query,
                page = page,
                size = size,
            )
        }
}
