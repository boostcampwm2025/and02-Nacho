package com.andlife.domain.model

data class Address(
    val id: Long,
    val roadAddress: String,
    val placeName: String,
    val streetAddress: String,
    val zipCode: String,
    val latitude: Double,
    val longitude: Double
)
