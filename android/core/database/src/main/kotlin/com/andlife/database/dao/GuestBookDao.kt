package com.andlife.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.andlife.database.entity.GuestBookEntity

@Dao
interface GuestBookDao {

    @Query("SELECT * FROM guest_book ORDER BY createdAt DESC")
    fun pagingSource(): PagingSource<Int, GuestBookEntity>

    @Upsert
    suspend fun upsertAll(guestBooks: List<GuestBookEntity>)

    @Query("DELETE FROM guest_book WHERE id = :guestBookId")
    suspend fun deleteById(guestBookId: Long)

    @Query("DELETE FROM guest_book")
    suspend fun clearAll()
}
