package com.andlife.nacho.di

import com.andlife.deeplink.di.AppsFlyerDevKey
import com.andlife.deeplink.di.KakaoNativeKey
import com.andlife.network.di.KakaoApiKey
import com.andlife.nacho.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiKeyModule {
    @Provides
    @Singleton
    @KakaoApiKey
    fun provideKakaoApiKey(): String = BuildConfig.KAKAO_REST_API_KEY

    @Provides
    @Singleton
    @KakaoNativeKey
    fun provideKakaoNativeKey(): String = BuildConfig.KAKAO_NATIVE_APP_KEY

    @Provides
    @Singleton
    @AppsFlyerDevKey
    fun provideAppsFlyerDevKey(): String = BuildConfig.APPSFLYER_DEV_KEY
}
