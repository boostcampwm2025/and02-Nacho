package com.andlife.fcm

import com.andlife.domain.util.FcmTokenManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FcmModule {
    @Binds
    @Singleton
    abstract fun bindFcmTokenManager(fcmTokenManager: FcmTokenManagerImpl): FcmTokenManager
}

