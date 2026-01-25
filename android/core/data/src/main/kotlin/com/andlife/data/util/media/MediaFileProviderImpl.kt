package com.andlife.data.util.media

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import com.andlife.domain.model.guestbook.MediaFile
import com.andlife.domain.model.guestbook.MediaType
import com.andlife.domain.util.MediaFileProvider
import jakarta.inject.Inject
import java.io.File

class MediaFileProviderImpl
@Inject
constructor(
    private val contentResolver: ContentResolver,
) : MediaFileProvider {
    // Uri로부터 MediaFileInfo 생성
    override fun createFromUri(uriString: String): MediaFile? {
        val uri = uriString.toUri()
        
        // file:// URI인 경우 직접 파일에서 정보 추출
        if (uri.scheme == "file") {
            val file = File(uri.path ?: return null)
            if (!file.exists()) return null
            
            val mediaType = MediaType.fromExtension(file.extension)
            return MediaFile(
                uriString = uri.toString(),
                fileName = file.name,
                fileSize = file.length(),
                mediaType = mediaType,
            )
        }
        
        // content:// URI인 경우 기존 방식 사용
        val (fileName, fileSize) = getFileInfoFromUri(uri) ?: return null
        val mediaType = getMediaTypeFromUri(uri)

        return MediaFile(
            uriString = uri.toString(),
            fileName = fileName,
            fileSize = fileSize,
            mediaType = mediaType,
        )
    }

    override fun createFromUris(uriStrings: List<String>): List<MediaFile> =
        uriStrings.mapNotNull { createFromUri(it) }

    override fun createFromFile(file: File): MediaFile {
        val extension = file.extension.lowercase()
        val mediaType = MediaType.fromExtension(extension)

        return MediaFile(
            uriString = "file://${file.absolutePath}", // file:// URI 형태로 변환
            fileName = file.name,
            fileSize = file.length(),
            mediaType = mediaType,
        )
    }

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

        MediaType.fromMimeType(mimeType)?.let { return it }

        val extension = getExtensionFromUri(uri)
        return MediaType.fromExtension(extension)
    }

    // Uri로부터 파일 확장자 추출
    private fun getExtensionFromUri(uri: Uri): String {
        // 스템 MimeTypeMap에서 먼저 찾기
        val mimeType = contentResolver.getType(uri)
        val extensionFromMap = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)

        if (!extensionFromMap.isNullOrEmpty()) return extensionFromMap

        // MimeTypeMap에 없는 경우, 파일명에서 직접 추출
        val extension =
            when (uri.scheme) {
                "content" -> {
                    contentResolver
                        .query(
                            uri,
                            arrayOf(OpenableColumns.DISPLAY_NAME),
                            null,
                            null,
                            null,
                        )?.use { cursor ->
                            if (cursor.moveToFirst()) {
                                val displayName = cursor.getString(0)
                                displayName.substringAfterLast('.', "")
                            } else {
                                ""
                            }
                        } ?: ""
                }

                else -> {
                    uri.path?.substringAfterLast('.', "") ?: ""
                }
            }
        return extension.lowercase()
    }
}
