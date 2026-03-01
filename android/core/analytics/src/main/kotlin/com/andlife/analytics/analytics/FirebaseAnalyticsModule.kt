package com.andlife.analytics.analytics

import com.andlife.domain.util.AnalyticsLogger
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseAnalyticsProvideModule {

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(): FirebaseAnalytics {
        return Firebase.analytics
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class FirebaseAnalyticsBindModule {
    @Binds
    @Singleton
    abstract fun bindFirebaseAnalyticsLogger(
        firebaseAnalyticsLogger: FirebaseAnalyticsLogger
    ): AnalyticsLogger
}

