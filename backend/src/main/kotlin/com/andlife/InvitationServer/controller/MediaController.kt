package com.andlife.InvitationServer.controller

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
import software.amazon.awssdk.services.s3.model.HeadObjectRequest
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

data class UploadUrlRequest(
    val mediaType: MediaType,
    val fileName: String
)

data class UploadUrlResponse(
    val uploadUrl: String,
    val mediaKey: String,
    val mediaType: MediaType
)

data class CompleteUploadRequest(
    val mediaKey: String,
    val mediaType: MediaType
)

data class CompleteUploadResponse(
    val success: Boolean,
    val mediaUrl: String? = null,
    val message: String? = null
)

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

@RestController
@RequestMapping("/api/v1/media")
class MediaController(
    private val r2Client: S3Client,
    private val r2Presigner: S3Presigner,
    @param:Value("\${r2.bucket-name}") private val bucketName: String,
    @param:Value("\${r2.public-url}") private val publicUrl: String
) {

    // 1. Pre-signed URL 생성 (업로드용)
    @PostMapping("/start")
    fun getUploadUrl(@RequestBody request: UploadUrlRequest): UploadUrlResponse {
        val mediaType = request.mediaType
        val extension = mediaType.getExtension()

        // 미디어 타입별로 폴더 분리
        val key = "${mediaType.folder}/${UUID.randomUUID()}$extension"

        // Pre-signed URL 생성 (15분 유효)
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

        return UploadUrlResponse(
            uploadUrl = presignedRequest.url().toString(),
            mediaKey = key,
            mediaType = mediaType
        )
    }

    @PostMapping("/complete")
    fun completeUpload(@RequestBody request: CompleteUploadRequest): CompleteUploadResponse {
        return try {
            // 파일이 실제로 R2에 존재하는지 확인
             val headObjectRequest = HeadObjectRequest.builder()
                 .bucket(bucketName)
                 .key(request.mediaKey)
                 .build()
             r2Client.headObject(headObjectRequest)

            val mediaUrl = if (publicUrl.isNotBlank()) {
                "$publicUrl/${request.mediaKey}"
            } else {
                null
            }

            CompleteUploadResponse(
                success = true,
                mediaUrl = mediaUrl,
                message = "성공적으로 업로드가 완료되었습니다."
            )
        } catch (e: Exception) {
            CompleteUploadResponse(
                success = false,
                message = "업로드 검증 실패: ${e.message}"
            )
        }
    }

    @DeleteMapping("/{mediaKey}")
    fun deleteMedia(@PathVariable mediaKey: String): CompleteUploadResponse {
        return try {
            r2Client.deleteObject { builder ->
                builder.bucket(bucketName)
                    .key(mediaKey)
            }

            CompleteUploadResponse(
                success = true,
                message = "Media deleted successfully"
            )
        } catch (e: Exception) {
            CompleteUploadResponse(
                success = false,
                message = "Delete failed: ${e.message}"
            )
        }
    }
}