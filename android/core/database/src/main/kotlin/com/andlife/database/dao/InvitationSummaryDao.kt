package com.andlife.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.andlife.database.entity.InvitationSummaryEntity

@Dao
interface InvitationSummaryDao {

    @Query(
        """
          SELECT * FROM invitation_summary
          WHERE status = :status AND isMyInvitation = :isMyInvitation
          ORDER BY invitationDate ASC, startTime ASC
      """
    )
    fun pagingSourceAsc(
        status: String,
        isMyInvitation: Boolean,
    ): PagingSource<Int, InvitationSummaryEntity>

    @Query(
        """
          SELECT * FROM invitation_summary
          WHERE status = :status AND isMyInvitation = :isMyInvitation
          ORDER BY invitationDate DESC, startTime DESC
      """
    )
    fun pagingSourceDesc(
        status: String,
        isMyInvitation: Boolean,
    ): PagingSource<Int, InvitationSummaryEntity>

    @Upsert
    suspend fun upsertAll(invitations: List<InvitationSummaryEntity>)

    @Query(
        """
          DELETE FROM invitation_summary
          WHERE status = :status AND isMyInvitation = :isMyInvitation
      """
    )
    suspend fun clearByQuery(status: String, isMyInvitation: Boolean)

    @Query("DELETE FROM invitation_summary WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM invitation_summary")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM invitation_summary WHERE status = :status AND isMyInvitation = :isMyInvitation")
    suspend fun count(status: String, isMyInvitation: Boolean): Int
}
