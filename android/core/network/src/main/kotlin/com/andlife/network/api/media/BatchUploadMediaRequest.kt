@file:OptIn(InternalSerializationApi::class)

package com.andlife.network.api.media

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
data class BatchUploadMediaRequest(
    val files: List<FileUploadInfoRequest>,
)

@Serializable
data class FileUploadInfoRequest(
    val fileName: String,
    val fileSize: Long,
    val mediaType: String, // "VIDEO", "IMAGE", "AUDIO"
)
