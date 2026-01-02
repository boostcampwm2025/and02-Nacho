package com.andlife.data.repository.address

import com.andlife.domain.model.Address
import com.andlife.network.api.kakao.address.Document

fun Document.toDomain(): Address =
    Address(
        id = id.toLongOrNull() ?: 0L,
        roadAddressName = roadAddressName,
        placeName = placeName,
        addressName = addressName,
        zipCode = "",
        latitude = latitude.toDoubleOrNull() ?: 0.0,
        longitude = longitude.toDoubleOrNull() ?: 0.0,
    )
