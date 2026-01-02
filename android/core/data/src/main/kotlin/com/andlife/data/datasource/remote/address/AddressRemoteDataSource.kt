package com.andlife.data.datasource.remote.address

import com.andlife.network.api.kakao.address.KakaoAddressResponse

interface AddressRemoteDataSource {
    suspend fun searchAddress(
        query: String,
        page: Int,
        size: Int,
    ): KakaoAddressResponse
}
