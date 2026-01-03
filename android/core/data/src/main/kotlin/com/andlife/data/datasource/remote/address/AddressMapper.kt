package com.andlife.data.datasource.remote.address

import com.andlife.domain.model.Address
import com.andlife.network.api.kakao.address.KakaoAddressResponse

fun KakaoAddressResponse.Document.toDomain(): Address =
    Address(
        id = id.toLongOrNull() ?: 0L,
        roadAddressName = roadAddressName,
        placeName = placeName,
        addressName = addressName,
        zipCode = "",
        latitude = latitude.toDoubleOrNull() ?: 0.0,
        longitude = longitude.toDoubleOrNull() ?: 0.0,
    )
