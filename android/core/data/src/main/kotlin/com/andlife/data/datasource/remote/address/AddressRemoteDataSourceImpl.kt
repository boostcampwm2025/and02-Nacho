package com.andlife.data.datasource.remote.address

import com.andlife.network.api.kakao.address.KakaoAddressResponse
import com.andlife.network.api.kakao.address.KakaoAddressService
import javax.inject.Inject
import javax.inject.Named

class AddressRemoteDataSourceImpl @Inject constructor(
    private val kakaoAddressService: KakaoAddressService,
    @param:Named("kakaoApiKey") private val apiKey: String,
) : AddressRemoteDataSource {
    override suspend fun searchAddress(
        query: String,
        page: Int,
        size: Int,
    ): KakaoAddressResponse =
        kakaoAddressService.searchAddress(
            authorization = "KakaoAK $apiKey",
            query = query,
            page = page,
            size = size,
        )
}
