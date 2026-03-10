package com.andlife.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.andlife.database.entity.HomeGuestBookEntity

@Dao
interface HomeGuestBookDao {

    @Query("SELECT * FROM home_guest_book ORDER BY createdAt DESC")
    fun pagingSource(): PagingSource<Int, HomeGuestBookEntity>

    @Upsert
    suspend fun upsertAll(guestBooks: List<HomeGuestBookEntity>)

    @Query("DELETE FROM home_guest_book WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM home_guest_book")
    suspend fun clearAll()
}
