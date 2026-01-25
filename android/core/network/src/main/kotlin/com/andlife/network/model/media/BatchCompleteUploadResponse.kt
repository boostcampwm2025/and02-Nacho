@file:OptIn(InternalSerializationApi::class)

package com.andlife.network.model.media

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
data class BatchCompleteUploadResponse(
    val files: List<CompletedFileInfoResponse>,
)

@Serializable
data class CompletedFileInfoResponse(
    val fileName: String,
    val mediaKey: String,
    val mediaUrl: String?,
    val success: Boolean,
)

@Serializable
data class DeleteMediaResponse(
    val success: Boolean,
    val message: String? = null,
)
