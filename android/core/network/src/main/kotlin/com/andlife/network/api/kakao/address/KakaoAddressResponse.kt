@file:OptIn(InternalSerializationApi::class)

package com.andlife.network.api.kakao.address

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KakaoAddressResponse(
    @SerialName("meta") val meta: Meta,
    @SerialName("documents") val documents: List<Document>,
) {
    @Serializable
    data class Meta(
        @SerialName("total_count") val totalCount: Int,
        @SerialName("pageable_count") val pageableCount: Int,
        @SerialName("is_end") val isEnd: Boolean,
    )

    @Serializable
    data class Document(
        @SerialName("id") val id: String,
        @SerialName("place_name") val placeName: String,
        @SerialName("address_name") val addressName: String,
        @SerialName("road_address_name") val roadAddressName: String,
        @SerialName("x") val longitude: String,
        @SerialName("y") val latitude: String,
    )
}
