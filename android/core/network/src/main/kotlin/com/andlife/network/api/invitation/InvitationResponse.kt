@file:OptIn(InternalSerializationApi::class)

package com.andlife.network.api.invitation

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InvitationResponse(
    @SerialName("id") val id: Long,
    @SerialName("host_id") val hostId: Long,
    @SerialName("title") val title: String,
    @SerialName("display_host_name") val displayHostName: String,
    @SerialName("thumbnail_urls") val thumbnailUrls: List<String>,
    @SerialName("invitation_date") val invitationDate: String,
    @SerialName("start_time") val startTime: String,
    @SerialName("end_time") val endTime: String?,
    @SerialName("placename") val placename: String,
    @SerialName("address") val address: String,
    @SerialName("lat") val lat: Double,
    @SerialName("lng") val lng: Double,
    @SerialName("location_guide") val locationGuide: String?,
    @SerialName("host_profile_url") val hostProfileUrl: String?,
    @SerialName("invitation_card") val invitationCard: InvitationCardResponse?,
    @SerialName("announcements") val announcements: List<AnnouncementResponse>,
)

@Serializable
data class InvitationCardResponse(
    @SerialName("id") val id: Long,
    @SerialName("invitation_id") val invitationId: Long,
    @SerialName("content_json") val contentJson: String,
    @SerialName("background_image_url") val backgroundImageUrl: String?,
)

@Serializable
data class AnnouncementResponse(
    @SerialName("id") val id: Long,
    @SerialName("invitation_id") val invitationId: Long,
    @SerialName("title") val title: String,
    @SerialName("content") val content: String,
    @SerialName("display_order") val displayOrder: Int,
)
