package com.andlife.nacho.di

import com.andlife.nacho.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiKeyModule {
    @Provides
    @Singleton
    @Named("kakaoApiKey")
    fun provideKakaoApiKey(): String = BuildConfig.KAKAO_REST_API_KEY
}
