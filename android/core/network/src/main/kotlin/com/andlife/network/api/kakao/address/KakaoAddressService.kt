package com.andlife.network.api.kakao.address

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoAddressService {
    @GET("v2/local/search/keyword.json")
    suspend fun searchAddress(
        @Header("Authorization") authorization: String,
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): KakaoAddressResponse
}
