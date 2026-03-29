package com.andlife.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.andlife.database.entity.UpcomingInvitationEntity

@Dao
interface UpcomingInvitationDao {

    @Query("SELECT * FROM upcoming_invitation ORDER BY invitationDate ASC, startTime ASC")
    fun pagingSource(): PagingSource<Int, UpcomingInvitationEntity>

    @Upsert
    suspend fun upsertAll(invitations: List<UpcomingInvitationEntity>)

    @Query("DELETE FROM upcoming_invitation WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM upcoming_invitation")
    suspend fun clearAll()
}
