package com.andlife.network.api.media

import com.andlife.network.model.BaseResponse
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface MediaService {

    // ==================== 단건 API ====================

    @POST("/api/v1/media/start")
    suspend fun uploadMedia(
        @Body request: UploadMediaRequest
    ): BaseResponse<UploadMediaResponse>

    @POST("/api/v1/media/complete")
    suspend fun completeUpload(
        @Body request: CompleteUploadRequest
    ): BaseResponse<CompleteUploadResponse>

    // ==================== 배치 API ====================

    @POST("/api/v1/media/batch/start")
    suspend fun batchUploadMedia(
        @Body request: BatchUploadMediaRequest
    ): BaseResponse<BatchUploadMediaResponse>

    @POST("/api/v1/media/batch/complete")
    suspend fun batchCompleteUpload(
        @Body request: BatchCompleteUploadRequest
    ): BaseResponse<BatchCompleteUploadResponse>

    // ==================== 삭제 API ====================

    @DELETE("/api/v1/media/{mediaKey}")
    suspend fun deleteMedia(
        @Path("mediaKey") mediaKey: String
    ): BaseResponse<CompleteUploadResponse>
}

// ==================== 단건 DTO ====================

@Serializable
data class UploadMediaRequest(
    val mediaType: String,  // "VIDEO", "IMAGE", "AUDIO"
    val fileName: String
)

@Serializable
data class UploadMediaResponse(
    val uploadUrl: String,
    val mediaKey: String,
    val mediaType: String
)

@Serializable
data class CompleteUploadRequest(
    val mediaKey: String,
    val mediaType: String
)

@Serializable
data class CompleteUploadResponse(
    val success: Boolean,
    val mediaUrl: String? = null,
    val message: String? = null
)

// ==================== 배치 DTO ====================

@Serializable
data class BatchUploadMediaRequest(
    val files: List<FileInfo>
)

@Serializable
data class FileInfo(
    val mediaType: String,  // "VIDEO", "IMAGE", "AUDIO"
    val fileName: String
)

@Serializable
data class BatchUploadMediaResponse(
    val files: List<UploadInfo>
)

@Serializable
data class UploadInfo(
    val uploadUrl: String,
    val mediaKey: String,
    val mediaType: String,
    val fileName: String
)

@Serializable
data class BatchCompleteUploadRequest(
    val files: List<FileKeyInfo>
)

@Serializable
data class FileKeyInfo(
    val mediaKey: String,
    val fileName: String
)

@Serializable
data class BatchCompleteUploadResponse(
    val files: List<CompleteUploadInfo>
)

@Serializable
data class CompleteUploadInfo(
    val mediaKey: String,
    val mediaUrl: String?,
    val fileName: String
)
