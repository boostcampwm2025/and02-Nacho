package com.andlife.model.invitation

import com.andlife.model.editor.NachoUiCard
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.LocalDate

data class InvitationContentsUiModel(
    val title: String = "",
    val hostInfo: HostInfo = HostInfo(),
    val imageList: ImmutableList<String> = persistentListOf(),
    val dateTime: DateTimeInfo = DateTimeInfo(),
    val location: LocationInfo = LocationInfo(),
    val announcement: ImmutableList<AnnouncementUiModel>? = null,
    val invitationCard: InvitationCardUiModel? = null,
)

data class HostInfo(
    val name: String = "",
    val profileUrl: String? = null,
)

data class DateTimeInfo(
    val date: LocalDate = LocalDate(2026, 1, 1),
    val startTime: TimeUiModel = TimeUiModel(0, 0),
)

data class TimeUiModel(
    val hour: Int,
    val min: Int,
)

data class LocationInfo(
    val name: String = "",
    val address: String = "",
    val guide: String? = null,
    val latLng: LatLngUiModel = LatLngUiModel(),
)

data class LatLngUiModel(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
)

data class AnnouncementUiModel(
    val id: Long = 0L,
    val title: String,
    val content: String,
)

data class InvitationCardUiModel(
    val id: Long = 0L,
    val invitationId: Long = 0L,
    val card: NachoUiCard = NachoUiCard.empty(),
    val backgroundColor: Long = 0L,
    val backgroundImageUrl: String? = null,
)
