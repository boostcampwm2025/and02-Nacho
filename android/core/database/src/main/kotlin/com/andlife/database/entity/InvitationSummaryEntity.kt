package com.andlife.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.andlife.database.DatabaseConstants

@Entity(tableName = DatabaseConstants.TABLE_INVITATION_SUMMARY)
data class InvitationSummaryEntity(
    @PrimaryKey
    val id: Long,
    val title: String,
    val displayHostName: String,
    val thumbnailUrls: List<String>,
    val invitationDate: String,
    val startTime: String,
    val address: String,
    val isOwner: Boolean,
    val status: String,
    val isMyInvitation: Boolean,
)
