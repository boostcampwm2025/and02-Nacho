package com.andlife.database.di

import android.content.Context
import androidx.room.Room
import com.andlife.database.InvitationDatabase
import com.andlife.database.dao.InvitationSummaryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DATABASE_NAME = "invitation.db"

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): InvitationDatabase {
        return Room.databaseBuilder(
            context,
            InvitationDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideInvitationSummaryDao(db: InvitationDatabase): InvitationSummaryDao {
        return db.invitationSummaryDao()
    }
}
