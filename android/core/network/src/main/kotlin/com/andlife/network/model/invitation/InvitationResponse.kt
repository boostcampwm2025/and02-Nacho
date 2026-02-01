@file:OptIn(InternalSerializationApi::class)

package com.andlife.network.model.invitation

import com.andlife.network.model.thankscard.ThanksCardResponse
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
data class InvitationResponse(
    val id: Long,
    val hostId: Long,
    val title: String,
    val displayHostName: String,
    val thumbnailUrls: List<String>,
    val invitationDate: String,
    val startTime: String,
    val endTime: String?,
    val placename: String,
    val address: String,
    val lat: Double,
    val lng: Double,
    val locationGuide: String?,
    val hostProfileUrl: String?,
    val invitationCard: InvitationCardResponse?,
    val thanksCard: ThanksCardResponse?,
    val announcements: List<AnnouncementResponse>,
)

@Serializable
data class InvitationCardResponse(
    val id: Long,
    val invitationId: Long,
    val contentJson: String,
    val backgroundColor: Long,
    val backgroundImageUrl: String?,
)

@Serializable
data class AnnouncementResponse(
    val id: Long,
    val invitationId: Long,
    val title: String,
    val content: String,
    val displayOrder: Int,
)
