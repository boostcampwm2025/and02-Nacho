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
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.net.URI
import java.time.Duration
import java.util.UUID

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
    IMAGE("images", "image/jpeg"),
    AUDIO("audios", "audio/mpeg");

    fun getExtension(): String {
        return when (this) {
            VIDEO -> ".mp4"
            IMAGE -> ".jpg"
            AUDIO -> ".mp3"
        }
    }
}

data class UploadMediaRequest(
    val mediaType: String,
    val fileName: String
)

data class UploadMediaResponse(
    val uploadUrl: String,
    val mediaKey: String,
    val mediaType: String
)

data class CompleteUploadRequest(
    val mediaKey: String,
    val mediaType: String
)

data class CompleteUploadResponse(
    val success: Boolean,
    val mediaUrl: String? = null,
    val message: String? = null
)

data class BatchUploadMediaRequest(
    val files: List<FileInfo>
)

data class FileInfo(
    val mediaType: String,
    val fileName: String
)

data class BatchUploadMediaResponse(
    val files: List<UploadInfo>
)

data class UploadInfo(
    val uploadUrl: String,
    val mediaKey: String,
    val mediaType: String,
    val fileName: String
)

data class BatchCompleteUploadRequest(
    val files: List<FileKeyInfo>
)

data class FileKeyInfo(
    val mediaKey: String,
    val fileName: String
)

data class BatchCompleteUploadResponse(
    val files: List<CompleteUploadInfo>
)

data class CompleteUploadInfo(
    val mediaKey: String,
    val mediaUrl: String?,
    val fileName: String
)

@RestController
@RequestMapping("/api/v1/media")
class MediaController(
    private val r2Client: S3Client,
    private val r2Presigner: S3Presigner,
    @param:Value("\${r2.bucket-name}") private val bucketName: String,
    @param:Value("\${r2.public-url}") private val publicUrl: String
) {
    @PostMapping("/start")
    fun getUploadUrl(@RequestBody request: UploadMediaRequest): BaseResponse<UploadMediaResponse> {
        return try {
            val mediaType = MediaType.valueOf(request.mediaType.uppercase())
            val extension = mediaType.getExtension()
            val key = "${mediaType.folder}/${UUID.randomUUID()}$extension"

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

            val response = UploadMediaResponse(
                uploadUrl = presignedRequest.url().toString(),
                mediaKey = key,
                mediaType = mediaType.name
            )

            BaseResponse.success(response)
        } catch (e: IllegalArgumentException) {
            BaseResponse.success(
                UploadMediaResponse(
                    uploadUrl = "",
                    mediaKey = "",
                    mediaType = ""
                ),
                responseCode = CommonResponseCode.BAD_REQUEST
            )
        } catch (e: Exception) {
            BaseResponse.success(
                UploadMediaResponse(
                    uploadUrl = "",
                    mediaKey = "",
                    mediaType = ""
                ),
                responseCode = CommonResponseCode.INTERNAL_SERVER_ERROR
            )
        }
    }

    @PostMapping("/complete")
    fun completeUpload(@RequestBody request: CompleteUploadRequest): BaseResponse<CompleteUploadResponse> {
        return try {
            val mediaUrl = if (publicUrl.isNotBlank()) {
                "$publicUrl/${request.mediaKey}"
            } else {
                null
            }

            val response = CompleteUploadResponse(
                success = true,
                mediaUrl = mediaUrl
            )

            BaseResponse.success(response)
        } catch (e: Exception) {
            val response = CompleteUploadResponse(
                success = false,
                message = "Upload completion failed: ${e.message}"
            )
            BaseResponse.success(response, responseCode = CommonResponseCode.INTERNAL_SERVER_ERROR)
        }
    }

    @PostMapping("/batch/start")
    fun getBatchUploadUrls(
        @RequestBody request: BatchUploadMediaRequest
    ): BaseResponse<BatchUploadMediaResponse> {
        return try {
            val uploadInfos = request.files.map { fileInfo ->
                val mediaType = MediaType.valueOf(fileInfo.mediaType.uppercase())
                val extension = mediaType.getExtension()
                val key = "${mediaType.folder}/${UUID.randomUUID()}$extension"

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

                UploadInfo(
                    uploadUrl = presignedRequest.url().toString(),
                    mediaKey = key,
                    mediaType = mediaType.name,
                    fileName = fileInfo.fileName
                )
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
    fun completeBatchUpload(
        @RequestBody request: BatchCompleteUploadRequest
    ): BaseResponse<BatchCompleteUploadResponse> {
        return try {
            val results = request.files.map { fileKey ->
                val mediaUrl = if (publicUrl.isNotBlank()) {
                    "$publicUrl/${fileKey.mediaKey}"
                } else {
                    null
                }

                CompleteUploadInfo(
                    mediaKey = fileKey.mediaKey,
                    mediaUrl = mediaUrl,
                    fileName = fileKey.fileName
                )
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

    @DeleteMapping("/{mediaKey}")
    fun deleteMedia(@PathVariable mediaKey: String): BaseResponse<CompleteUploadResponse> {
        return try {
            r2Client.deleteObject { builder ->
                builder.bucket(bucketName)
                    .key(mediaKey)
            }

            val response = CompleteUploadResponse(
                success = true,
                mediaUrl = null
            )

            BaseResponse.success(response)
        } catch (e: Exception) {
            val response = CompleteUploadResponse(
                success = false,
                message = "Media deletion failed: ${e.message}"
            )
            BaseResponse.success(response, responseCode = CommonResponseCode.INTERNAL_SERVER_ERROR)
        }
    }
}