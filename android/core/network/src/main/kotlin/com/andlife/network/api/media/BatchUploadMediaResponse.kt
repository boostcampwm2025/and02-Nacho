@file:OptIn(InternalSerializationApi::class)

package com.andlife.network.api.media

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
data class BatchUploadMediaResponse(
    val files: List<UploadInfoResponse>,
)

@Serializable
data class UploadInfoResponse(
    val fileName: String,
    val mediaKey: String,
    val mediaType: String,
    val isMultipart: Boolean,
    // Simple upload
    val uploadUrl: String? = null,
    // Multipart upload
    val uploadId: String? = null,
    val chunkSize: Long? = null,
    val totalChunks: Int? = null,
    val chunkUrls: List<ChunkUrlResponse>? = null,
)

@Serializable
data class ChunkUrlResponse(
    val partNumber: Int,
    val uploadUrl: String,
)
