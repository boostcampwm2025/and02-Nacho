package com.andlife.data.datasource.remote.di

import com.andlife.data.datasource.remote.address.AddressRemoteDataSource
import com.andlife.data.datasource.remote.address.AddressRemoteDataSourceImpl
import com.andlife.network.api.kakao.address.KakaoAddressService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AddressRemoteDataSourceModule {
    @Provides
    @Singleton
    fun provideAddressRemoteDataSource(
        kakaoAddressService: KakaoAddressService,
        @Named("kakaoApiKey") apiKey: String,
    ): AddressRemoteDataSource =
        AddressRemoteDataSourceImpl(
            kakaoAddressService = kakaoAddressService,
            apiKey = apiKey,
        )
}
