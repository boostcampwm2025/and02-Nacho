package com.andlife.invitationzzang.di

import com.andlife.invitationzzang.BuildConfig
import com.andlife.network.di.KakaoApiKey
import com.andlife.network.di.KakaoNativeKey
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
}
