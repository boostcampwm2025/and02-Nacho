package com.andlife.model.invitation

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.LocalDate

data class InvitationContentsUiModel(
    val title: String = "",
    val hostInfo: HostInfo = HostInfo(),
    val imageList: ImmutableList<String> = persistentListOf(),
    val dateTime: DateTimeInfo = DateTimeInfo(),
    val location: LocationInfo = LocationInfo(),
    val announcement: ImmutableList<AnnouncementUiModel> = persistentListOf(),
    val invitationCard: InvitationCardUiModel? = null,
)

data class HostInfo(
    val name: String = "",
    val profileUrl: String = "",
)

data class DateTimeInfo(
    val date: LocalDate? = null,
    val startTime: InvitationTimeUiModel? = null,
)

data class LocationInfo(
    val name: String = "",
    val address: String = "",
    val guide: String = "",
    val latLng: LatLngUiModel = LatLngUiModel(),
)

data class LatLngUiModel(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
)

data class InvitationTimeUiModel(
    val hour: Int,
    val min: Int,
)

data class AnnouncementUiModel(
    val id: String = "",
    val title: String,
    val content: String,
)

data class InvitationCardUiModel(
    val id: String = "",
    val invitationId: Long = 0L,
    val contentJson: String = "",
    val backgroundImageUrl: String? = null,
)
