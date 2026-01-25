package com.andlife.network.api.kakao.address

import com.andlife.network.model.kakao.address.KakaoAddressResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface KakaoAddressService {
    @GET("v2/local/search/keyword.json")
    suspend fun searchAddress(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): KakaoAddressResponse
}
