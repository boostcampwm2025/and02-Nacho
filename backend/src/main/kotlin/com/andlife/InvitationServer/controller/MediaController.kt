package com.andlife.InvitationServer.controller

import com.andlife.InvitationServer.response.BaseResponse
import com.andlife.InvitationServer.response.CommonResponseCode
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest
import software.amazon.awssdk.services.s3.model.CompletedMultipartUpload
import software.amazon.awssdk.services.s3.model.CompletedPart
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.UploadPartRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import software.amazon.awssdk.services.s3.presigner.model.UploadPartPresignRequest
import java.net.URI
import java.time.Duration
import java.util.UUID
import kotlin.math.ceil

@Configuration
class R2Config {

    @Value("\${r2.access-key}")
    private lateinit var accessKey: String

    @Value("\${r2.secret-key}")
    private lateinit var secretKey: String

    @Value("\${r2.endpoint}")
    private lateinit var endpoint: String

    @Value("\${r2.region}")
    private lateinit var region: String

    @Bean
    fun r2Client(): S3Client {
        val awsCredentials = AwsBasicCredentials.create(accessKey, secretKey)

        return S3Client.builder()
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
            .endpointOverride(URI.create(endpoint))
            .build()
    }

    @Bean
    fun r2Presigner(): S3Presigner {
        val awsCredentials = AwsBasicCredentials.create(accessKey, secretKey)

        return S3Presigner.builder()
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
            .endpointOverride(URI.create(endpoint))
            .build()
    }
}

enum class MediaType(val folder: String, val contentType: String) {
    VIDEO("videos", "video/mp4"),
    IMAGE("images", "image/webp"),
    AUDIO("audios", "audio/mpeg");

    fun getExtension(): String {
        return when (this) {
            VIDEO -> ".mp4"
            IMAGE -> ".webp"
            AUDIO -> ".mp3"
        }
    }
}

data class BatchUploadMediaRequest(
    val files: List<FileUploadInfo>
)

data class FileUploadInfo(
    val fileName: String,
    val fileSize: Long,
    val mediaType: String
)

data class BatchUploadMediaResponse(
    val files: List<UploadInfo>
)

data class UploadInfo(
    val fileName: String,
    val mediaKey: String,
    val mediaType: String,
    val isMultipart: Boolean,

    // Simple upload (100MB 이하)
    val uploadUrl: String? = null,

    // Multipart upload (100MB 초과)
    val uploadId: String? = null,
    val chunkSize: Long? = null,
    val totalChunks: Int? = null,
    val chunkUrls: List<ChunkUrl>? = null
)

data class ChunkUrl(
    val partNumber: Int,
    val uploadUrl: String
)

data class BatchCompleteUploadRequest(
    val files: List<CompleteFileInfo>
)

data class CompleteFileInfo(
    val mediaKey: String,
    val fileName: String,
    val uploadId: String? = null, // multipart인 경우만
    val parts: List<PartInfo>? = null // multipart인 경우만
)

data class PartInfo(
    val partNumber: Int,
    val eTag: String
)

data class BatchCompleteUploadResponse(
    val files: List<CompletedFileInfo>
)

data class CompletedFileInfo(
    val fileName: String,
    val mediaKey: String,
    val mediaUrl: String?,
    val success: Boolean
)

data class DeleteMediaResponse(
    val success: Boolean,
    val message: String? = null
)

@RestController
@RequestMapping("/api/v1/media")
class MediaController(
    private val r2Client: S3Client,
    private val r2Presigner: S3Presigner,
    @param:Value("\${r2.bucket-name}") private val bucketName: String,
    @param:Value("\${r2.public-url}") private val publicUrl: String
) {
    @PostMapping("/batch/start")
    fun batchStartUpload(
        @RequestBody request: BatchUploadMediaRequest
    ): BaseResponse<BatchUploadMediaResponse> {
        return try {
            val uploadInfos = request.files.map { fileInfo ->
                val mediaType = MediaType.valueOf(fileInfo.mediaType.uppercase())
                val extension = mediaType.getExtension()
                val timestamp = System.currentTimeMillis()
                val key = "${mediaType.folder}/${timestamp}-${UUID.randomUUID()}$extension"

                val isLargeVideo = mediaType == MediaType.VIDEO && fileInfo.fileSize > SIZE_THRESHOLD_BYTES

                if (isLargeVideo) {
                    createMultipartUploadInfo(key, mediaType, fileInfo.fileName, fileInfo.fileSize)
                } else {
                    createSimpleUploadInfo(key, mediaType, fileInfo.fileName)
                }
            }

            val response = BatchUploadMediaResponse(files = uploadInfos)
            BaseResponse.success(response)

        } catch (e: IllegalArgumentException) {
            BaseResponse.success(
                BatchUploadMediaResponse(files = emptyList()),
                responseCode = CommonResponseCode.BAD_REQUEST
            )
        } catch (e: Exception) {
            BaseResponse.success(
                BatchUploadMediaResponse(files = emptyList()),
                responseCode = CommonResponseCode.INTERNAL_SERVER_ERROR
            )
        }
    }

    @PostMapping("/batch/complete")
    fun batchCompleteUpload(
        @RequestBody request: BatchCompleteUploadRequest
    ): BaseResponse<BatchCompleteUploadResponse> {
        return try {
            val results = request.files.map { fileInfo ->
                try {
                    // Multipart 업로드인 경우
                    if (fileInfo.uploadId != null && fileInfo.parts != null) {
                        r2Client.completeMultipartUpload(
                            CompleteMultipartUploadRequest.builder()
                                .bucket(bucketName)
                                .key(fileInfo.mediaKey)
                                .uploadId(fileInfo.uploadId)
                                .multipartUpload(
                                    CompletedMultipartUpload.builder()
                                        .parts(fileInfo.parts.map { part ->
                                            CompletedPart.builder()
                                                .partNumber(part.partNumber)
                                                .eTag(part.eTag)
                                                .build()
                                        })
                                        .build()
                                )
                                .build()
                        )
                    }
                    // Simple 업로드는 클라이언트가 이미 완료했으므로 별도 처리 불필요

                    val mediaUrl = if (publicUrl.isNotBlank()) {
                        "${publicUrl.removeSuffix("/")}/${fileInfo.mediaKey.removePrefix("/")}"
                    } else {
                        null
                    }

                    CompletedFileInfo(
                        fileName = fileInfo.fileName,
                        mediaKey = fileInfo.mediaKey,
                        mediaUrl = mediaUrl,
                        success = true
                    )
                } catch (e: Exception) {
                    CompletedFileInfo(
                        fileName = fileInfo.fileName,
                        mediaKey = fileInfo.mediaKey,
                        mediaUrl = null,
                        success = false
                    )
                }
            }

            val response = BatchCompleteUploadResponse(files = results)
            BaseResponse.success(response)

        } catch (e: Exception) {
            BaseResponse.success(
                BatchCompleteUploadResponse(files = emptyList()),
                responseCode = CommonResponseCode.INTERNAL_SERVER_ERROR
            )
        }
    }

    private fun createSimpleUploadInfo(
        key: String,
        mediaType: MediaType,
        fileName: String
    ): UploadInfo {
        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType(mediaType.contentType)
            .build()

        val presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(15))
            .putObjectRequest(putObjectRequest)
            .build()

        val presignedRequest = r2Presigner.presignPutObject(presignRequest)

        return UploadInfo(
            fileName = fileName,
            mediaKey = key,
            mediaType = mediaType.name,
            isMultipart = false,
            uploadUrl = presignedRequest.url().toString()
        )
    }

    private fun createMultipartUploadInfo(
        key: String,
        mediaType: MediaType,
        fileName: String,
        fileSize: Long
    ): UploadInfo {
        val initiateRequest = CreateMultipartUploadRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType(mediaType.contentType)
            .build()

        val initiateResponse = r2Client.createMultipartUpload(initiateRequest)
        val uploadId = initiateResponse.uploadId()

        val totalChunks = ceil(fileSize / CHUNK_SIZE.toDouble()).toInt()

        val chunkUrls = (1..totalChunks).map { partNumber ->
            val uploadPartRequest = UploadPartRequest.builder()
                .bucket(bucketName)
                .key(key)
                .uploadId(uploadId)
                .partNumber(partNumber)
                .build()

            val presignedRequest = r2Presigner.presignUploadPart(
                UploadPartPresignRequest.builder()
                    .signatureDuration(Duration.ofHours(2))
                    .uploadPartRequest(uploadPartRequest)
                    .build()
            )

            ChunkUrl(
                partNumber = partNumber,
                uploadUrl = presignedRequest.url().toString()
            )
        }

        return UploadInfo(
            fileName = fileName,
            mediaKey = key,
            mediaType = mediaType.name,
            isMultipart = true,
            uploadId = uploadId,
            chunkSize = CHUNK_SIZE,
            totalChunks = totalChunks,
            chunkUrls = chunkUrls
        )
    }

    companion object {
        private const val SIZE_THRESHOLD_MB = 100L
        private const val SIZE_THRESHOLD_BYTES = SIZE_THRESHOLD_MB * 1024 * 1024
        private const val CHUNK_SIZE = 10 * 1024 * 1024L // 10MB
    }
}