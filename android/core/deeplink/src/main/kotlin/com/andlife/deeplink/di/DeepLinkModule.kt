package com.andlife.deeplink.di

import com.andlife.deeplink.DeepLinkManager
import com.andlife.deeplink.DeepLinkManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DeepLinkModule {
    @Binds
    @Singleton
    abstract fun bindDeepLinkManager(deepLinkManagerImpl: DeepLinkManagerImpl): DeepLinkManager
}
