package com.andlife.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddressUiModel(
    val id: Long,
    val roadAddress: String,
    val placeName: String,
    val streetAddress: String,
    val zipCode: String,
    val latitude: Double,
    val longitude: Double
) : Parcelable
