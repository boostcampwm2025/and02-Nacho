package com.andlife.network.di

import com.andlife.network.api.kakao.address.KakaoAddressService
import com.andlife.network.interceptor.KakaoAuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object KakaoNetworkModule {
    private const val KAKAO_BASE_URL = "https://dapi.kakao.com/"

    @Provides
    @Singleton
    @KakaoOkHttp
    fun provideKakaoOkHttpClient(
        kakaoAuthInterceptor: KakaoAuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(kakaoAuthInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()

    @Provides
    @Singleton
    @KakaoRetrofit
    fun provideKakaoRetrofit(
        @KakaoOkHttp okHttpClient: OkHttpClient,
        json: Json,
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(KAKAO_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides
    @Singleton
    fun provideKakaoAddressService(
        @KakaoRetrofit retrofit: Retrofit,
    ): KakaoAddressService = retrofit.create(KakaoAddressService::class.java)
}
