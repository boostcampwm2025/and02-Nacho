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
          WHERE status = :status
          AND isMyInvitation = :isMyInvitation
          AND sortType = :sortType
          ORDER BY invitationDate ASC, startTime ASC
      """
    )
    fun pagingSourceAsc(
        status: String,
        isMyInvitation: Boolean,
        sortType: String
    ): PagingSource<Int, InvitationSummaryEntity>

    @Query(
        """
          SELECT * FROM invitation_summary
          WHERE status = :status
          AND isMyInvitation = :isMyInvitation
          AND sortType = :sortType
          ORDER BY invitationDate DESC, startTime DESC
      """
    )
    fun pagingSourceDesc(
        status: String,
        isMyInvitation: Boolean,
        sortType: String
    ): PagingSource<Int, InvitationSummaryEntity>

    @Upsert
    suspend fun upsertAll(invitations: List<InvitationSummaryEntity>)

    @Query(
        """
          DELETE FROM invitation_summary
          WHERE status = :status
          AND isMyInvitation = :isMyInvitation
          AND sortType = :sortType
      """
    )
    suspend fun clearByQuery(status: String, isMyInvitation: Boolean, sortType: String)
}
