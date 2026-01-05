package com.andlife.network.api.media

import kotlinx.serialization.Serializable

// @Serializable
// data class UploadMediaResponse(
//    val uploadUrl: String,
//    val mediaKey: String,
//    val mediaType: String
// )

@Serializable
data class BatchUploadMediaRequest(
    val files: List<FileUploadInfo>,
)

@Serializable
data class FileUploadInfo(
    val fileName: String,
    val fileSize: Long,
    val mediaType: String, // "VIDEO", "IMAGE", "AUDIO"
)

@Serializable
data class BatchCompleteUploadRequest(
    val files: List<CompleteFileInfo>,
)

@Serializable
data class CompleteFileInfo(
    val mediaKey: String,
    val fileName: String,
    val uploadId: String? = null,
    val parts: List<PartInfo>? = null,
)

@Serializable
data class PartInfo(
    val partNumber: Int,
    val eTag: String,
)

// ==================== Response DTOs ====================

@Serializable
data class BatchUploadMediaResponse(
    val files: List<UploadInfo>,
)

@Serializable
data class UploadInfo(
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
    val chunkUrls: List<ChunkUrl>? = null,
)

@Serializable
data class ChunkUrl(
    val partNumber: Int,
    val uploadUrl: String,
)

@Serializable
data class BatchCompleteUploadResponse(
    val files: List<CompletedFileInfo>,
)

@Serializable
data class CompletedFileInfo(
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
