package com.andlife.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.andlife.database.converter.Converters
import com.andlife.database.dao.InvitationSummaryDao
import com.andlife.database.entity.InvitationSummaryEntity

@Database(
    entities = [InvitationSummaryEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class InvitationDatabase : RoomDatabase() {
    abstract fun invitationSummaryDao(): InvitationSummaryDao
}
