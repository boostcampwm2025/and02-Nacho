package com.andlife.domain.model.address

data class Address(
    val id: Long,
    val roadAddressName: String,
    val placeName: String,
    val addressName: String,
    val zipCode: String,
    val latitude: Double,
    val longitude: Double
)
