package com.andlife.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.andlife.database.converter.Converters
import com.andlife.database.dao.GuestBookDao
import com.andlife.database.dao.InvitationSummaryDao
import com.andlife.database.dao.UpcomingInvitationDao
import com.andlife.database.entity.GuestBookEntity
import com.andlife.database.entity.InvitationSummaryEntity
import com.andlife.database.entity.UpcomingInvitationEntity

@Database(
    entities = [
        InvitationSummaryEntity::class,
        UpcomingInvitationEntity::class,
        GuestBookEntity::class,
    ],
    version = DatabaseConstants.DATABASE_VERSION,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class InvitationDatabase : RoomDatabase() {
    abstract fun invitationSummaryDao(): InvitationSummaryDao
    abstract fun upcomingInvitationDao(): UpcomingInvitationDao
    abstract fun guestBookDao(): GuestBookDao
}
