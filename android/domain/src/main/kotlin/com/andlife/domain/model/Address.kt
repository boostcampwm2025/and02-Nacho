package com.andlife.domain.model

data class Address(
    val id: Int,
    val roadAddress: String,
    val placeName: String,
    val streetAddress: String,
    val zipCode: String
)
