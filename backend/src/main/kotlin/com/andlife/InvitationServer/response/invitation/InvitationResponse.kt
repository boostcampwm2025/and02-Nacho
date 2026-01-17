package com.andlife.InvitationServer.response.invitation

import com.fasterxml.jackson.annotation.JsonProperty

data class InvitationResponse(
    val id: Long,
    @JsonProperty("host_id")
    val hostId: Long,
    val title: String,
    @JsonProperty("display_host_name")
    val displayHostName: String,
    @JsonProperty("host_profile_url")
    val hostProfileUrl: String?,
    @JsonProperty("thumbnail_urls")
    val thumbnailUrls: List<String>,
    @JsonProperty("invitation_date")
    val invitationDate: String,
    @JsonProperty("start_time")
    val startTime: String,
    @JsonProperty("end_time")
    val endTime: String?,
    val placename: String,
    val address: String,
    val lat: Double,
    val lng: Double,
    @JsonProperty("location_guide")
    val locationGuide: String?,
    @JsonProperty("invitation_card")
    val invitationCard: InvitationCardResponse?,
    val announcements: List<AnnouncementResponse>,
)

data class InvitationCardResponse(
    val id: Long,
    @JsonProperty("invitation_id")
    val invitationId: Long,
    @JsonProperty("content_json")
    val contentJson: String,
    @JsonProperty("background_image_url")
    val backgroundImageUrl: String?,
)

data class AnnouncementResponse(
    val id: Long,
    @JsonProperty("invitation_id")
    val invitationId: Long,
    val title: String,
    val content: String,
    @JsonProperty("display_order")
    val displayOrder: Int,
)
