package com.andlife.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.andlife.database.DatabaseConstants

@Entity(tableName = DatabaseConstants.TABLE_HOME_GUEST_BOOK)
data class HomeGuestBookEntity(
    @PrimaryKey
    val id: Long,
    val invitationId: Long,
    val authorId:Long,
    val authorName: String,
    val authorProfileUrl: String?,
    val invitationTitle: String?,
    val textContent: String,
    val totalVisualCount: Int,
    val isOwner: Boolean,
    val isInvitationOwner: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val visualMedias: List<MediaCache>,
    val audioMedias: List<MediaCache>,
)
