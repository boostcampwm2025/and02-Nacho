package com.andlife.myinvitation.model

import android.os.Parcelable
import com.andlife.domain.model.Address
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddressUiModel(
    val id: Long,
    val roadAddress: String,
    val placeName: String,
    val streetAddress: String,
    val zipCode: String,
) : Parcelable

fun Address.toUiModel() = AddressUiModel(
    id = id,
    roadAddress = roadAddress,
    placeName = placeName,
    streetAddress = streetAddress,
    zipCode = zipCode
)

fun AddressUiModel.toDomain() = Address(
    id = id,
    roadAddress = roadAddress,
    placeName = placeName,
    streetAddress = streetAddress,
    zipCode = zipCode
)
