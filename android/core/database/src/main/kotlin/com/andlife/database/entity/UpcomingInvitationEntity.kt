package com.andlife.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "upcoming_invitation")
data class UpcomingInvitationEntity(
    @PrimaryKey
    val id: Long,
    val hostId: Long,
    val isOwner: Boolean,
    val title: String,
    val thumbnailUrl: String?,
    val invitationDate: String,
    val startTime: String,
    val displayHostName: String,
)
