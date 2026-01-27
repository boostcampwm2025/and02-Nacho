package com.andlife.nachoserver.response.invitation

import com.andlife.nachoserver.entity.AnnouncementSection
import com.andlife.nachoserver.entity.Invitation
import com.andlife.nachoserver.entity.InvitationCard

data class InvitationResponse(
    val id: Long,
    val hostId: Long,
    val title: String,
    val displayHostName: String,
    val hostProfileUrl: String?,
    val thumbnailUrls: List<String>,
    val invitationDate: String,
    val startTime: String,
    val endTime: String?,
    val placename: String,
    val address: String,
    val lat: Double,
    val lng: Double,
    val locationGuide: String?,
    val invitationCard: InvitationCardResponse?,
    val announcements: List<AnnouncementResponse>,
)

data class InvitationCardResponse(
    val id: Long,
    val invitationId: Long,
    val contentJson: String,
    val backgroundColor: Long,
    val backgroundImageUrl: String?,
)

data class AnnouncementResponse(
    val id: Long,
    val invitationId: Long,
    val title: String,
    val content: String,
    val displayOrder: Int,
)

fun Invitation.toInvitationResponse(
    card: InvitationCard? = null,
    announcements: List<AnnouncementSection> = emptyList()
): InvitationResponse {
    return InvitationResponse(
        id = this.id,
        hostId = this.host.id,
        title = this.title,
        displayHostName = this.displayHostName,
        hostProfileUrl = this.host.profileImageUrl,
        thumbnailUrls = this.thumbnailUrls,
        invitationDate = this.invitationDate.toString(),
        startTime = this.startTime.toString(),
        endTime = this.endTime?.toString(),
        placename = this.placeName,
        address = this.address,
        lat = this.lat,
        lng = this.lng,
        locationGuide = this.locationGuide,
        invitationCard = card?.let {
            InvitationCardResponse(
                id = it.id,
                invitationId = this.id,
                contentJson = it.contentJson,
                backgroundColor = it.backgroundColor,
                backgroundImageUrl = it.backgroundImageUrl
            )
        },
        announcements = announcements.map {
            AnnouncementResponse(
                id = it.id,
                invitationId = this.id,
                title = it.title,
                content = it.content,
                displayOrder = it.displayOrder
            )
        }
    )
}