package com.andlife.network.api.media

import kotlinx.serialization.Serializable

@Serializable
data class UploadMediaResponse(
    val uploadUrl: String,
    val mediaKey: String,
    val mediaType: String
)
