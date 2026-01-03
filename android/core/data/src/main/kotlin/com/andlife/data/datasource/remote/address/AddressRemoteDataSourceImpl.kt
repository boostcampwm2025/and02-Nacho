package com.andlife.data.datasource.remote.address

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
    ): KakaoAddressResponse =
        kakaoAddressService.searchAddress(
            query = query,
            page = page,
            size = size,
        )
}
