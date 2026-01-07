@file:OptIn(InternalSerializationApi::class)

package com.andlife.network.api.media

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
data class BatchCompleteUploadRequest(
    val files: List<CompleteFileInfoRequest>,
)

@Serializable
data class CompleteFileInfoRequest(
    val mediaKey: String,
    val fileName: String,
    val uploadId: String? = null,
    val parts: List<PartInfoRequest>? = null,
)

@Serializable
data class PartInfoRequest(
    val partNumber: Int,
    val eTag: String,
)
