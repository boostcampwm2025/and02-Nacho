package com.andlife.network.model.thankscard

import kotlinx.serialization.Serializable

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class ThanksCardRequest(
    val contentJson: String,
    val backgroundColor: Long,
    val backgroundImageUrl: String?,
)
