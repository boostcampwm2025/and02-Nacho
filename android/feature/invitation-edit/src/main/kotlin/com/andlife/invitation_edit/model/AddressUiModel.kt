package com.andlife.invitation_edit.model

import android.os.Parcelable
import com.andlife.domain.model.Address
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddressUiModel(
    val id: Long,
    val roadAddressName: String,
    val placeName: String,
    val addressName: String,
    val zipCode: String,
    val latitude: Double,
    val longitude: Double,
) : Parcelable

fun Address.toUiModel() =
    AddressUiModel(
        id = id,
        roadAddressName = roadAddressName,
        placeName = placeName,
        addressName = addressName,
        zipCode = zipCode,
        latitude = latitude,
        longitude = longitude,
    )
