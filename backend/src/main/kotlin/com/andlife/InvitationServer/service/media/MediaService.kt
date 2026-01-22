package com.andlife.InvitationServer.service.media

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.s3.S3Client

@Service
class MediaService(
    private val r2Client: S3Client,
    @Value($$"${r2.bucket-name}") private val bucketName: String,
    @Value($$"${r2.public-url}") private val publicUrl: String
) {
    fun deleteMedia(mediaKey: String) {
        if (mediaKey.isBlank()) return
        try {
            r2Client.deleteObject { it.bucket(bucketName).key(mediaKey) }
        } catch (e: Exception) {
            println("미디어 삭제 실패: $mediaKey, 오류: ${e.message}")
        }
    }

    fun extractKey(url: String?): String {
        if (url.isNullOrBlank()) return ""
        val baseUrl = publicUrl.removeSuffix("/")
        return url.substringAfter("$baseUrl/").removePrefix("/")
    }
}