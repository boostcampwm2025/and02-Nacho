package com.andlife.analytics.crash

import com.andlife.domain.util.CrashlyticsLogger
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.crashlytics
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object FirebaseCrashlyticsProvideModule {
    @Provides
    fun provideFirebaseCrashlytics(): FirebaseCrashlytics {
        return Firebase.crashlytics
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class FirebaseCrashlyticsBindModule {
    @Binds
    abstract fun bindFirebaseCrashlyticsLogger(
        firebaseCrashlyticsLogger: FirebaseCrashlyticsLogger
    ): CrashlyticsLogger
}
