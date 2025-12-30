package com.andlife.myinvitation.mapper

import com.andlife.domain.model.Address
import com.andlife.ui.model.AddressUiModel

fun Address.toUiModel() =
    AddressUiModel(
        id = id,
        roadAddress = roadAddress,
        placeName = placeName,
        streetAddress = streetAddress,
        zipCode = zipCode,
        latitude = latitude,
        longitude = longitude,
    )
