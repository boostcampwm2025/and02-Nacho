package com.andlife.data.util.media

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.net.toUri
import com.andlife.domain.model.MediaType
import jakarta.inject.Inject

class MediaFileProvider @Inject constructor(
    private val contentResolver: ContentResolver,
) {
    // Uri로부터 MediaFileInfo 생성
    fun createFromUri(uriString: String): MediaFile? {
        val uri = uriString.toUri()
        val (fileName, fileSize) = getFileInfoFromUri(uri) ?: return null
        val mediaType = getMediaTypeFromUri(uri)

        return MediaFile(
            uriString = uri.toString(),
            fileName = fileName,
            fileSize = fileSize,
            mediaType = mediaType,
        )
    }

    fun createFromUris(uriStrings: List<String>): List<MediaFile> = uriStrings.mapNotNull { createFromUri(it) }

    // Uri로부터 파일 이름과 크기 가져오기
    private fun getFileInfoFromUri(uri: Uri): Pair<String, Long>? =
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)

            if (cursor.moveToFirst() && nameIndex != -1 && sizeIndex != -1) {
                val name = cursor.getString(nameIndex)
                val size = cursor.getLong(sizeIndex)
                name to size
            } else {
                null
            }
        }

    // Uri로부터 미디어 타입 결정
    private fun getMediaTypeFromUri(uri: Uri): MediaType {
        val mimeType = contentResolver.getType(uri)

        return when {
            mimeType?.startsWith("image/") == true -> MediaType.IMAGE
            mimeType?.startsWith("video/") == true -> MediaType.VIDEO
            mimeType?.startsWith("audio/") == true -> MediaType.AUDIO
            else -> {
                val extension = getExtensionFromUri(uri)
                when (extension.lowercase()) {
                    "jpg", "jpeg", "png", "gif", "webp" -> MediaType.IMAGE
                    "mp4", "mov", "avi", "mkv" -> MediaType.VIDEO
                    "mp3", "wav", "m4a", "aac" -> MediaType.AUDIO
                    else -> MediaType.IMAGE
                }
            }
        }
    }

    // Uri로부터 파일 확장자 가져오기
    private fun getExtensionFromUri(uri: Uri): String =
        when (uri.scheme) {
            "content" -> {
                contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (displayNameIndex != -1) {
                            val displayName = cursor.getString(displayNameIndex)
                            displayName.substringAfterLast('.', "")
                        } else {
                            ""
                        }
                    } else {
                        ""
                    }
                } ?: ""
            }
            else -> uri.path?.substringAfterLast('.', "") ?: ""
        }
}
