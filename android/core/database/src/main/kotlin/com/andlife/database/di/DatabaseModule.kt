package com.andlife.database.di

import android.content.Context
import androidx.room.Room
import com.andlife.database.DatabaseConstants
import com.andlife.database.InvitationDatabase
import com.andlife.database.dao.GuestBookDao
import com.andlife.database.dao.HomeGuestBookDao
import com.andlife.database.dao.InvitationSummaryDao
import com.andlife.database.dao.UpcomingInvitationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): InvitationDatabase {
        return Room.databaseBuilder(
            context,
            InvitationDatabase::class.java,
            DatabaseConstants.DATABASE_NAME
        ).fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideInvitationSummaryDao(db: InvitationDatabase): InvitationSummaryDao {
        return db.invitationSummaryDao()
    }

    @Provides
    @Singleton
    fun provideUpcomingInvitationDao(db: InvitationDatabase): UpcomingInvitationDao {
        return db.upcomingInvitationDao()
    }

    @Provides
    @Singleton
    fun provideHomeGuestBookDao(db: InvitationDatabase): HomeGuestBookDao {
        return db.homeGuestBookDao()
    }

    @Provides
    @Singleton
    fun provideGuestBookDao(db: InvitationDatabase): GuestBookDao {
        return db.guestBookDao()
    }
}
