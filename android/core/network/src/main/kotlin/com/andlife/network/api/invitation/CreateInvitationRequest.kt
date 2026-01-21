@file:OptIn(InternalSerializationApi::class)
package com.andlife.network.api.invitation

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
data class CreateInvitationRequest(
    val title: String,
    val displayHostName: String,
    val thumbnailUrls: List<String>,
    val invitationDate: String,
    val startTime: String,
    val endTime: String?,
    val placename: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val locationGuide: String?,
    val invitationCard: InvitationCardRequest?,
    val announcements: List<AnnouncementRequest>,
)

@Serializable
data class InvitationCardRequest(
    val contentJson: String,
    val backgroundColor: Long,
    val backgroundImageUrl: String?,
)

@Serializable
data class AnnouncementRequest(
    val title: String,
    val content: String,
    val displayOrder: Int,
)
