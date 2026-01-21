package com.andlife.data.di

import com.andlife.data.auth.AuthTokenProviderImpl
import com.andlife.network.auth.AuthTokenProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {
    @Binds
    abstract fun bindAuthTokenProvider(
        impl: AuthTokenProviderImpl
    ): AuthTokenProvider
}
