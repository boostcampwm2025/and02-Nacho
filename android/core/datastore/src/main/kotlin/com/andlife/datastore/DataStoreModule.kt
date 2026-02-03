package com.andlife.datastore

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val PREFS_DATASTORE_USER = "user_prefs"
private val Context.userDataSource by preferencesDataStore(name = PREFS_DATASTORE_USER)

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @UserDataStore
    @Provides
    @Singleton
    fun provideUserDataStore(@ApplicationContext context: Context) = context.userDataSource
}
